# Earth's Heartbeat

An Android app + home-screen widget for the Schumann // ELF-VLF dashboard.

- **App**: full-screen WebView of `https://pole-shift.observer/app2/index.html`,
  auto-reloading every hour (the page also self-refreshes).
- **Splash / icon**: uses `1.png` from the deployment (fetched during CI; a
  matrix-themed placeholder is committed so the project builds without it).
- **Widget**: pick *any* Schumann / VLF / geomagnetic feed to show on your home
  screen. Each widget instance remembers its own choice and refreshes hourly via
  WorkManager. Tap it to open the full dashboard. Images route through the same
  `proxy.php` the dashboard uses, so the `http://` vlf.it / etna feeds load fine.

## Build the APK (recommended: GitHub Actions)

1. Create a repo and push this folder.
2. The workflow in `.github/workflows/build.yml` runs on push. It:
   - installs the Android SDK,
   - downloads the live `1.png` and regenerates the launcher icon + splash,
   - builds a debug APK,
   - uploads it as the artifact **`EarthsHeartbeat-apk`** containing
     **`Earth's Heartbeat.apk`**.
3. Open the run → **Artifacts** → download → sideload onto your phone
   (enable "install unknown apps").

You can also trigger it manually from the **Actions** tab (**Run workflow**).

## Build locally

Requires Android Studio (Giraffe+) or a local Gradle + Android SDK.

```bash
# Android Studio: just "Open" this folder — it provisions the wrapper + SDK,
# then Build > Build APK(s).

# Or from the command line (SDK + gradle installed):
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

To swap in the real logo locally, drop a square `1.png` into
`app/src/main/res/drawable-nodpi/splash_logo.png` and the `mipmap-*/ic_launcher.png`
files (or just let CI do it).

## Release build (optional)

For a Play-signed / distributable APK, add a keystore and a `signingConfig` in
`app/build.gradle`, then `./gradlew assembleRelease`. The debug APK above is
already installable for personal sideloading.

## Config

Everything feed-related lives in `Feeds.kt` — labels, sources, URLs, and the
proxy base. Edit there to add or reorder feeds.
