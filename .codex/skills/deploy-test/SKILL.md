---
name: deploy-test
description: Build and install BongDaTV on a connected Android TV emulator or device for testing. Use when the user says "test on device", "deploy", "install", "run on TV", "try it", or asks to verify behavior on the emulator.
---

# Deploy For Testing

Build and install the debug APK on a connected Android TV device or emulator.

## Steps

1. Check for a connected device:

   ```bash
   adb devices
   ```

   If no device is found, check whether the emulator is running. Start the Android TV AVD if needed:

   ```bash
   ~/Library/Android/sdk/emulator/emulator -avd TV_API34 -no-window -no-audio -gpu swiftshader_indirect &
   ```

   Wait for the device to come online:

   ```bash
   adb wait-for-device
   ```

2. Build the debug APK:

   ```bash
   ./gradlew assembleDebug
   ```

3. Install on the device:

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

4. Launch the app:

   ```bash
   adb shell am start -n com.bongdatv/.MainActivity
   ```

5. Take a screenshot to verify:

   ```bash
   adb exec-out screencap -p > /tmp/bongdatv-screen.png
   ```

   Inspect the screenshot and report the result to the user.

## Notes

- Emulator AVD name: `TV_API34` using Android TV arm64-v8a API 34.
- If the emulator is not created yet, create it with:

  ```bash
  ~/Library/Android/sdk/cmdline-tools/latest/bin/avdmanager create avd -n TV_API34 -k "system-images;android-34;android-tv;arm64-v8a" -d tv_1080p
  ```
