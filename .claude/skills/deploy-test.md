---
name: deploy-test
description: Build and install the app on a connected Android TV emulator or device for testing. Use when the user says "test on device", "deploy", "install", "run on TV", or "try it".
---

# Deploy for Testing

Build and install the debug APK on a connected device/emulator.

## Steps

1. **Check for connected device**:
   ```bash
   adb devices
   ```
   If no device found, check if emulator is running. Start one if needed:
   ```bash
   ~/Library/Android/sdk/emulator/emulator -avd TV_API34 -no-window -no-audio -gpu swiftshader_indirect &
   ```
   Wait for device to come online: `adb wait-for-device`

2. **Build debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on device**:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

4. **Launch the app**:
   ```bash
   adb shell am start -n com.bongdatv/.MainActivity
   ```

5. **Take screenshot** to verify:
   ```bash
   adb exec-out screencap -p > /tmp/bongdatv-screen.png
   ```
   Show the screenshot to the user.

## Notes
- Emulator AVD name: `TV_API34` (android-tv arm64-v8a API 34)
- If emulator isn't created yet, create with:
  ```bash
  ~/Library/Android/sdk/cmdline-tools/latest/bin/avdmanager create avd -n TV_API34 -k "system-images;android-34;android-tv;arm64-v8a" -d tv_1080p
  ```
