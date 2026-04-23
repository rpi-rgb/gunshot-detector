package com.gunshot.detector

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gunshot.detector.features.EmergencyContactManager
import com.gunshot.detector.service.DetectionService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AlertEntry(val id: Int, val timeLabel: String, val confidence: Float, val confidenceLabel: String, val estimatedDirection: String? = null, val weaponType: String? = null)
data class UiState(val isListening: Boolean = false, val lastConfidence: Float = 0f, val alertHistory: List<AlertEntry> = emptyList(), val isProVersion: Boolean = false, val emergencyContacts: List<String> = emptyList())

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var alertCounter = 0
    private val emergencyContactManager = EmergencyContactManager(application)
    init { viewModelScope.launch { emergencyContactManager.contacts.collect { contacts -> _uiState.update { it.copy(emergencyContacts = contacts) } } } }
    fun startDetection() { val ctx = getApplication<Application>(); val intent = Intent(ctx, DetectionService::class.java).apply { action = DetectionService.ACTION_START }; ctx.startForegroundService(intent); _uiState.update { it.copy(isListening = true) } }
    fun stopDetection() { val ctx = getApplication<Application>(); val intent = Intent(ctx, DetectionService::class.java).apply { action = DetectionService.ACTION_STOP }; ctx.startService(intent); _uiState.update { it.copy(isListening = false) } }
    fun addAlert(confidence: Float, direction: Float? = null, weaponType: String? = null) { viewModelScope.launch { alertCounter++; val entry = AlertEntry(id = alertCounter, timeLabel = timeFormat.format(Date()), confidence = confidence, confidenceLabel = "${(confidence * 100).toInt()}%", estimatedDirection = direction?.let { "${it.toInt()}°" }, weaponType = weaponType); _uiState.update { state -> state.copy(lastConfidence = confidence, alertHistory = (listOf(entry) + state.alertHistory).take(50)) } } }
    fun addEmergencyContact(phoneNumber: String): Boolean = emergencyContactManager.addContact(phoneNumber)
    fun removeEmergencyContact(phoneNumber: String) = emergencyContactManager.removeContact(phoneNumber)
    fun setProVersion(isPro: Boolean) { _uiState.update { it.copy(isProVersion = isPro) } }
}