# 🤖 Universal Game Bot (Android Native Framework)

**Universal Game Bot** è una piattaforma Android nativa e modulare per l'osservazione, l'apprendimento, il test autonomo e l'automazione locale di giochi Android senza root.

---

## 🏗️ Architettura & Moduli

- **Core**: `ScreenCaptureManager`, `ActionEngine`, `VisionEngine`, `GameDetector`, `SafetyManager`, `BotLoop`.
- **Game Model**: `GameProfile`, `GameRegistry`, `GameAdapter` (include supporto per Sudoku, Crypto Game, Palmon, Solitaire, EA SPORTS FC Mobile, Amaze GO, ecc.).
- **Learning**: `LearningEngine`, `MemoryManager`, `LearningScore` (con fasce da Observation 0-39 a Autonomy Verified 100).
- **Planner & Verification**: `Planner`, `VerificationEngine`, `AutonomyCertificate`.
- **AI Router**: Zero-Cost Guard (funzionamento offline-first, nessun cloud AI obbligatorio).
- **Diagnostics & UI**: Jetpack Compose UI (Home, Games, Learn, Completed, Lab, AI, Settings, Game Details) e Diagnostics Manager.

---

## 📲 Installazione e Build

1. Requisiti: Android SDK 34, OpenJDK 17/21.
2. Compilazione con Gradle Wrapper:
   ```bash
   ./gradlew assembleDebug
   ```
3. L'APK generato (`app/build/outputs/apk/debug/app-debug.apk`) è installabile su qualsiasi dispositivo Android senza root.
