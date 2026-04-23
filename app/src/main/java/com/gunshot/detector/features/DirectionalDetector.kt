package com.gunshot.detector.features

class DirectionalDetector {
    data class DirectionEstimate(val azimuthDegrees: Float, val confidence: Float, val method: String)
    fun estimateSingleDevice(audioData: FloatArray, sampleRate: Int): DirectionEstimate? = null
    fun submitNetworkTimingData(onsetTimestampNanos: Long, anonymousDeviceToken: String) { }
}