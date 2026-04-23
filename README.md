# 🔫 Gunshot Detector — Android Application

**Privacy-first real-time gunshot detection and emergency reporting.**

---

## ⚠️ DISCLAIMER / AVERTISSEMENT

**This project is currently in the MVP (Minimum Viable Product) stage and has NOT been tested on a physical device yet.** 

**Ce projet est actuellement en phase MVP et n'a PAS encore été testé sur un appareil physique.** 

It is provided as-is for development and testing purposes. Do not rely on this application for life-safety situations until it has been thoroughly validated in real-world environments.

---

## 🚀 Features

- **Real-time AI Detection:** Uses Google's YAMNet (TensorFlow Lite) to identify gunshots from ambient audio.
- **Emergency Alerts:** Automatically sends SMS to pre-configured emergency contacts upon detection.
- **Privacy-First:** Designed to act as a sensor without tracking or sharing the user's exact position (anonymous distributed network concept).
- **Background Surveillance:** Runs as a Foreground Service with WakeLock optimization to monitor even when the screen is off.
- **Modern UI:** Built with Jetpack Compose in a sleek, tactical dark mode.

## 🛠️ Technical Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **IA:** TensorFlow Lite (YAMNet)
- **Services:** Android Foreground Services
- **Monetization:** AdMob & Play Billing (placeholders)

## 📥 Setup Instructions

1. **Clone the repository.**
2. **Download the model:** 
   - Go to [Kaggle - YAMNet](https://www.kaggle.com/models/google/yamnet/tfLite)
   - Download `yamnet.tflite` 
   - Place it in `app/src/main/assets/`.
3. **Open in Android Studio:** Let Gradle sync.
4. **Build & Run:** Test on a **physical device** (the emulator's microphone support is often insufficient for real-time analysis).

## 🗺️ Roadmap

- [ ] Implement TDOA triangulation for multi-device network.
- [ ] Add spectral analysis for weapon/caliber classification.
- [ ] Validate detection accuracy in various acoustic environments.

---

*Created with Wingman by Emergent Labs.*