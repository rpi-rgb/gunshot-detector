package com.gunshot.detector.features

import android.content.Context
import com.gunshot.detector.audio.DetectionResult

class AutomaticReporter(private val context: Context) {
    fun reportDetection(result: DetectionResult) { logDetectionLocally(result) }
    private fun logDetectionLocally(result: DetectionResult) { android.util.Log.d("AutomaticReporter", "Detection logged: confidence=${result.confidence}") }
}