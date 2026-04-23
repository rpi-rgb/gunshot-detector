package com.gunshot.detector.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gunshot.detector.AlertEntry
import com.gunshot.detector.UiState
import com.gunshot.detector.ui.theme.*
import com.gunshot.detector.ui.components.AdMobBanner

@Composable
fun MainScreen(
    uiState: UiState,
    onStartStop: () -> Unit,
    onBuyPro: () -> Unit,
    onAddEmergencyContact: (String) -> Boolean,
    onRemoveEmergencyContact: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .systemBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text(text = "GUNSHOT", color = AlertRed, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 8.sp)
        Text(text = "DETECTOR", color = OnSurfaceMuted, fontSize = 12.sp, letterSpacing = 6.sp)
        Spacer(Modifier.height(40.dp))
        PulsingMicButton(isActive = uiState.isListening, onClick = onStartStop)
        Spacer(Modifier.height(16.dp))
        Text(text = if (uiState.isListening) "● ACTIVE SURVEILLANCE" else "○ STANDBY", color = if (uiState.isListening) AlertRed else OnSurfaceMuted, fontSize = 12.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(12.dp)).padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem("ALERTS", uiState.alertHistory.size.toString())
            Divider(modifier = Modifier.height(40.dp).width(1.dp), color = SurfaceVariant)
            StatItem("LAST CONF.", if (uiState.alertHistory.isEmpty()) "--" else "${(uiState.lastConfidence * 100).toInt()}% ")
            Divider(modifier = Modifier.height(40.dp).width(1.dp), color = SurfaceVariant)
            StatItem("SOS", uiState.emergencyContacts.size.toString())
        }
        Spacer(Modifier.height(24.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { EmergencyContactsSection(contacts = uiState.emergencyContacts, onAddContact = onAddEmergencyContact, onRemoveContact = onRemoveEmergencyContact) }
            item { Spacer(Modifier.height(16.dp)); Text(text = "HISTORY", color = OnSurfaceMuted, fontSize = 10.sp, letterSpacing = 3.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) }
            if (uiState.alertHistory.isEmpty()) { item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("No alerts detected", color = OnSurfaceMuted, fontSize = 14.sp) } } } else { items(uiState.alertHistory) { alert -> AlertItem(alert) } }
        }
        Spacer(Modifier.height(8.dp))
        if (!uiState.isProVersion) { AdMobBanner(); Spacer(Modifier.height(4.dp)); TextButton(onClick = onBuyPro) { Text("Remove ads — \$1.99", color = OnSurfaceMuted, fontSize = 11.sp) } }
        Spacer(Modifier.height(8.dp))
    }
}