# Quick Grocery (MVP)

Minimal native Android quick-commerce app built with Kotlin, Jetpack Compose, MVVM, Repository pattern, Coroutines/Flow, Hilt, and Navigation Compose.

**Features**
- Home: search, category chips, product grid with Add
- Product detail: info, quantity stepper, add to cart
- Cart: item list with quantity controls, total, place order dialog
- In-memory sample data (no backend)

**Tech Stack**
- Kotlin + Gradle Kotlin DSL
- Jetpack Compose (Material 3)
- Hilt DI
- Coroutines/Flow
- Navigation Compose

## Open In Android Studio
1. Open Android Studio.
2. Select **Open** and choose this folder:
   `/home/fuzzyenigma/Desktop/Inventory/Android/Quick Grocery`
3. Let Gradle sync complete.

## Run On Emulator
1. Open **Device Manager** in Android Studio.
2. Create or start an emulator (API 24+).
3. Press **Run**.

## Install On Real Phone (USB)
1. Enable **Developer options** and **USB debugging** on the device.
2. Connect the device via USB and accept the RSA prompt.
3. Run:

```bash
adb devices
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting ADB
- **unauthorized**: Reconnect USB and accept the RSA prompt on the phone.
- **no device**: Ensure USB debugging is enabled and try a different cable/port.
- **missing platform-tools**: Install Android SDK Platform-Tools and ensure `adb` is in your PATH.

## Tests
```bash
./gradlew test
```
