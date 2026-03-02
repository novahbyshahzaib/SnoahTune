# SnoahTune

A local music player for Android built with Jetpack Compose, Media3/ExoPlayer, Room, and Hilt.

**Minimum Android version:** Android 7.0 (API 24)  
**Target Android version:** Android 15 (API 35)

---

## Installation

### Option 1 – Install the pre-built APK (fastest)

Every push to the `main` branch automatically triggers a GitHub Actions build that uploads a debug APK as a downloadable artifact. No local toolchain is required.

1. Open the repository on GitHub:
   `https://github.com/novahbyshahzaib/SnoahTune`

2. Click the **Actions** tab at the top of the page.

3. In the left sidebar, select the **Build SnoahTune APK** workflow.

4. Click the most recent successful run (green check mark).

5. Scroll to the **Artifacts** section at the bottom of the run summary page.

6. Click **SnoahTune-debug-apk** to download the ZIP archive.
   > Artifacts are kept for **30 days**. If the latest artifact has expired, trigger a new build manually: go to **Actions → Build SnoahTune APK**, click the **Run workflow** drop-down button, then click the **Run workflow** confirmation button.

7. Extract the ZIP to obtain `app-debug.apk`.

8. Transfer `app-debug.apk` to your Android device (USB, cloud storage, etc.).

9. On your Android device, enable **Install unknown apps** for the app you will use to open the file:
   - Go to **Settings → Apps → Special app access → Install unknown apps** (exact path varies by manufacturer).
   - Grant permission to your file manager or browser.

10. Open `app-debug.apk` on the device and tap **Install**.

11. Launch **SnoahTune** from the app drawer.

---

### Option 2 – Build and install from source with Android Studio

#### Prerequisites

| Tool | Version |
|------|---------|
| [Android Studio](https://developer.android.com/studio) | Hedgehog (2023.1.1) or newer |
| JDK | 17 (bundled with Android Studio) |
| Android SDK | API 35 (install via SDK Manager) |
| Android device or emulator | API 24+ |

#### Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/novahbyshahzaib/SnoahTune.git
   cd SnoahTune
   ```

2. **Open the project in Android Studio**

   - Launch Android Studio.
   - Choose **File → Open** and select the `SnoahTune` directory.
   - Wait for Gradle sync to finish (Android Studio downloads all dependencies automatically).

3. **Select a run target**

   - Connect a physical Android device via USB with **USB debugging** enabled  
     (*Settings → Developer options → USB debugging*), **or**
   - Create an Android Virtual Device (AVD) via **Tools → Device Manager → Create Device** (choose a device with API 24 or higher).

4. **Run the app**

   - Click the green **Run ▶** button in the toolbar, or press **Shift + F10**.
   - Android Studio builds the debug APK and installs it on your selected device automatically.

5. **Build a standalone APK without installing (optional)**

   - Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
   - The APK is saved to `app/build/outputs/apk/debug/app-debug.apk`.
   - Click **locate** in the notification that appears to open the folder.

---

### Option 3 – Build and install from source via the command line

#### Prerequisites

| Tool | Notes |
|------|-------|
| JDK 17 | Set `JAVA_HOME` to the JDK 17 installation directory |
| Android SDK | Set `ANDROID_HOME` (or `ANDROID_SDK_ROOT`) to the SDK root |
| `adb` | Included in `Android SDK/platform-tools`; add to `PATH` |

#### Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/novahbyshahzaib/SnoahTune.git
   cd SnoahTune
   ```

2. **Create `local.properties`** (tells Gradle where the Android SDK is)

   ```bash
   echo "sdk.dir=$ANDROID_HOME" > local.properties
   ```

   Replace `$ANDROID_HOME` with the actual path if the environment variable is not set, e.g.:
   - **macOS/Linux:** `echo "sdk.dir=/Users/<you>/Library/Android/sdk" > local.properties`
   - **Windows (PowerShell):** `"sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk" | Out-File local.properties -Encoding ascii`

3. **Generate the Gradle wrapper** (only needed if the `gradlew` file is absent)

   ```bash
   gradle wrapper --gradle-version 8.7 --distribution-type bin
   ```

4. **Build the debug APK**

   ```bash
   # macOS / Linux
   ./gradlew assembleDebug

   # Windows
   gradlew.bat assembleDebug
   ```

   The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

5. **Install the APK on a connected device**

   Ensure your device is connected via USB with USB debugging enabled and is visible to `adb`:

   ```bash
   adb devices
   ```

   Then install:

   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

   To replace an existing installation:

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

6. **Launch the app from the command line (optional)**

   ```bash
   adb shell am start -n com.snoahtune.app/.MainActivity
   ```

---

## Permissions

SnoahTune requests the following permissions at runtime:

| Permission | Purpose |
|-----------|---------|
| `READ_MEDIA_AUDIO` (API 33+) | Read audio files from device storage |
| `READ_EXTERNAL_STORAGE` (API ≤ 32) | Read audio files on older Android versions |
| `POST_NOTIFICATIONS` | Show media-playback notifications |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Keep music playing in the background |

Grant the storage/audio permission when prompted on first launch to allow the app to scan your music library.

---

## Troubleshooting

| Problem | Solution |
|---------|---------|
| "App not installed" error on device | Ensure **Install unknown apps** is enabled for the source app (see Option 1, step 9). |
| Gradle sync fails | Verify that `JAVA_HOME` points to JDK 17 and `ANDROID_HOME` points to a valid SDK installation with API 35 installed. |
| `adb: device not found` | Enable USB debugging on the device, accept the RSA key prompt, and re-run `adb devices`. |
| Build artifact expired (>30 days) | Trigger a new build via **Actions → Build SnoahTune APK → Run workflow**. |
| No audio files shown in app | Grant storage/audio permission in **Settings → Apps → SnoahTune → Permissions**. |
