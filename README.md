# SnoahTune

A local music player for Android built with Jetpack Compose, Media3/ExoPlayer, Room, and Hilt.

**Minimum Android version:** Android 7.0 (API 24)  
**Target Android version:** Android 15 (API 35)

---

## Installation

### Option 1 – Install the pre-built APK ✅ works on Android without a PC

Every push to the `main` branch automatically triggers a GitHub Actions build that produces a ready-to-install debug APK. All steps below can be completed entirely on your Android device using only a browser and a file manager.

> **Note:** Downloading GitHub Actions artifacts requires a free GitHub account. If you don't have one, create one at <https://github.com/join>.

#### Steps (Android device — no PC needed)

1. **Sign in to GitHub** in your Android browser (Chrome, Firefox, etc.) at
   `https://github.com/login`

2. Open the repository:
   `https://github.com/novahbyshahzaib/SnoahTune`

3. Tap the **Actions** tab (you may need to request the desktop site in your browser menu if the tab is not visible).

4. In the workflow list on the left, tap **Build SnoahTune APK**.

5. Tap the most recent run with a green ✅ check mark.

6. Scroll to the **Artifacts** section at the bottom of the page and tap **SnoahTune-debug-apk** to download the ZIP file to your device.
   > Artifacts are kept for **30 days**. If the artifact has expired, trigger a new build: tap the **Run workflow** drop-down button on the workflow page (while signed in), then confirm by tapping **Run workflow** again.

7. **Extract the APK from the ZIP.**  
   Open your file manager (e.g. *Files by Google*, *Mi File Manager*, *My Files*, or install [ZArchiver](https://play.google.com/store/apps/details?id=ru.zdevs.zarchiver) from the Play Store).  
   Locate the downloaded ZIP (usually in **Downloads**), tap it, and extract `app-debug.apk` to a folder you can find easily.

8. **Allow installation from unknown sources.**  
   Before opening the APK you need to grant your file manager (or browser) permission to install apps:
   - Go to **Settings → Apps → Special app access → Install unknown apps**.
   - Find the file manager or browser you are using and turn on **Allow from this source**.
   - *(Exact path varies by Android version and manufacturer — on some devices it is under **Settings → Security → Unknown sources**.)*

9. Tap `app-debug.apk` in your file manager and tap **Install**.

10. Launch **SnoahTune** from your app drawer.

---

### Option 2 – Build and install from source with Android Studio *(requires a PC/laptop)*

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

### Option 3 – Build and install from source via the command line *(requires a PC/laptop)*

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
| "App not installed" error on device | Ensure **Install unknown apps** is enabled for the app you used to open the APK (see Option 1, step 8). |
| Cannot see the Actions tab on mobile | In your browser menu, request the **Desktop site** (or full site) and reload the page. |
| No "Artifacts" section visible | You must be signed in to GitHub to see and download artifacts. Sign in and reload the page. |
| Cannot extract the ZIP on Android | Install a file manager that supports ZIP, such as [ZArchiver](https://play.google.com/store/apps/details?id=ru.zdevs.zarchiver) or *Files by Google*. |
| Gradle sync fails | Verify that `JAVA_HOME` points to JDK 17 and `ANDROID_HOME` points to a valid SDK installation with API 35 installed. |
| `adb: device not found` | Enable USB debugging on the device, accept the RSA key prompt, and re-run `adb devices`. |
| Build artifact expired (>30 days) | Sign in to GitHub, go to **Actions → Build SnoahTune APK**, and trigger a new run with **Run workflow**. |
| No audio files shown in app | Grant storage/audio permission in **Settings → Apps → SnoahTune → Permissions**. |
