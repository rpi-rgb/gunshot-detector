package com.gunshot.detector.features

class WeaponClassifier {
    data class WeaponProfile(val category: WeaponCategory, val estimatedCaliber: String?, val fireMode: FireMode, val confidence: Float)
    enum class WeaponCategory { HANDGUN, RIFLE, SHOTGUN, AUTOMATIC, UNKNOWN }
    enum class FireMode { SINGLE_SHOT, SEMI_AUTOMATIC, BURST, FULL_AUTOMATIC, UNKNOWN }
    fun classify(audioData: FloatArray, sampleRate: Int): WeaponProfile = WeaponProfile(WeaponCategory.UNKNOWN, null, FireMode.UNKNOWN, 0f)
}