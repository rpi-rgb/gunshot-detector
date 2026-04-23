package com.gunshot.detector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.gunshot.detector.billing.BillingManager
import com.gunshot.detector.ui.screens.MainScreen
import com.gunshot.detector.ui.theme.GunShotDetectorTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var billingManager: BillingManager
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions -> if (permissions.values.all { it }) viewModel.startDetection() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        billingManager = BillingManager(this) { viewModel.setProVersion(true) }
        setContent { GunShotDetectorTheme { val uiState by viewModel.uiState.collectAsState(); MainScreen(uiState = uiState, onStartStop = { if (uiState.isListening) viewModel.stopDetection() else checkAndRequestPermissions() }, onBuyPro = { billingManager.launchPurchaseFlow(this@MainActivity) }, onAddEmergencyContact = { phone -> viewModel.addEmergencyContact(phone) }, onRemoveEmergencyContact = { phone -> viewModel.removeEmergencyContact(phone) }) } }
    }
    private fun checkAndRequestPermissions() {
        val required = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SEND_SMS)
        val notGranted = required.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (notGranted.isEmpty()) viewModel.startDetection() else permissionLauncher.launch(notGranted.toTypedArray())
    }
}