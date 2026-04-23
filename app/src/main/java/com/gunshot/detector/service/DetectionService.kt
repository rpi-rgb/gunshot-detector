package com.gunshot.detector.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.gunshot.detector.MainActivity
import com.gunshot.detector.audio.AudioAnalyzer
import com.gunshot.detector.features.EmergencyContactManager
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DetectionService : Service() {
    private var audioAnalyzer: AudioAnalyzer? = null
    private var emergencyContactManager: EmergencyContactManager? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var lastSmsSentTimestamp = 0L
    private val smsCooldownMs = 30_000L
    private val vibrator by lazy { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator else getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    companion object { const val CHANNEL_ID = "GunShotDetector_Channel"; const val NOTIF_ID_FOREGROUND = 1; const val NOTIF_ID_ALERT = 2; const val ACTION_START = "ACTION_START_DETECTION"; const val ACTION_STOP = "ACTION_STOP_DETECTION" }
    override fun onCreate() { super.onCreate(); createNotificationChannel(); acquireWakeLock(); emergencyContactManager = EmergencyContactManager(this) }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { when (intent?.action) { ACTION_START -> startDetection(); ACTION_STOP -> stopDetection() }; return START_STICKY }
    private fun acquireWakeLock() { val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager; wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GunShotDetector:WakeLock"); wakeLock?.acquire(10 * 60 * 1000L) }
    private fun startDetection() { startForeground(NOTIF_ID_FOREGROUND, buildForegroundNotification()); audioAnalyzer = AudioAnalyzer(this).also { analyzer -> analyzer.startListening(); scope.launch { analyzer.detectionFlow.collect { result -> if (result.isGunshot) { triggerAlert(result.confidence); sendEmergencySms(result.confidence) } } } } }
    private fun stopDetection() { audioAnalyzer?.release(); audioAnalyzer = null; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
    private fun sendEmergencySms(confidence: Float) { val now = System.currentTimeMillis(); if (now - lastSmsSentTimestamp < smsCooldownMs) return; if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) return; val contacts = emergencyContactManager?.getContactsList() ?: return; if (contacts.isEmpty()) return; val message = "ALERTE GUNSHOT DETECTOR: Coups de feu détectés à ${timeFormat.format(Date())}. Confiance: ${(confidence * 100).toInt()}%"; val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) getSystemService(SmsManager::class.java) else SmsManager.getDefault(); for (contact in contacts) { try { smsManager.sendTextMessage(contact, null, message, null, null) } catch (e: Exception) { } }; lastSmsSentTimestamp = now }
    private fun triggerAlert(confidence: Float) { val pattern = longArrayOf(0, 200, 100, 200, 100, 500); if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1)) else vibrator.vibrate(pattern, -1); val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager; nm.notify(NOTIF_ID_ALERT, NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(android.R.drawable.ic_dialog_alert).setContentTitle("GUNSHOTS DETECTED").setContentText("Confidence: ${(confidence * 100).toInt()}%").setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).build()) }
    private fun buildForegroundNotification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Gunshot Detector").setContentText("Active surveillance…").setOngoing(true).build()
    private fun createNotificationChannel() { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager; nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Gunshot Detection", NotificationManager.IMPORTANCE_HIGH)) } }
    override fun onBind(intent: Intent?) = null
    override fun onDestroy() { audioAnalyzer?.release(); if (wakeLock?.isHeld == true) wakeLock?.release(); scope.cancel(); super.onDestroy() }
}