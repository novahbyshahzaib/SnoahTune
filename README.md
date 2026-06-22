# 🎵 SnoahTune

**SnoahTune** is a local music player for Android built entirely with modern Android technologies. It scans your device for audio files and lets you play, organize, and discover your personal music library — no internet connection required.

The UI follows a **Neu-Brutalism** design language: bold electric colours, thick black borders, flat offset drop-shadows, and all-caps typography for a look that stands out from typical Material apps.

---

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Suggested Future Features](#suggested-future-features)

---

## Features

### Playback
| Feature | Detail |
|---|---|
| **Play / Pause / Skip** | Standard transport controls via ExoPlayer |
| **Seek** | Drag the progress bar on the Now-Playing screen |
| **Shuffle** | Randomise the current queue |
| **Repeat** | Off → Repeat All → Repeat One cycle |
| **Playback Speed** | Adjustable from the Now-Playing screen |
| **Slowed + Reverb Mode** | Links pitch to speed so slowing down also lowers pitch, producing the popular "slowed-and-reverbed" aesthetic |
| **Background Playback** | Foreground `MediaSessionService` keeps music playing when the app is in the background |
| **Media Notification** | Lock-screen / notification controls powered by Media3 |

### Library
| Feature | Detail |
|---|---|
| **Auto-scan** | Reads all audio files from MediaStore on first launch or manual refresh |
| **Search** | Real-time filtering across title, artist, and album |
| **Sort** | Date Added (newest/oldest), Name (A–Z / Z–A), Duration (longest/shortest), Artist |
| **Favorites** | Toggle ♥ on any song; browse your favorites in the Library tab |
| **Playlists** | Create, rename, delete playlists; add any song to one or more playlists |
| **Albums** | Browse your library grouped by album |
| **Artists** | Browse your library grouped by artist |

### UI / UX
| Feature | Detail |
|---|---|
| **Mini Player** | Persistent bottom bar showing album art, title, artist, and a progress strip; swipe left/right to skip |
| **Now Playing Screen** | Full-screen player with large album art, song info, all transport controls, speed picker, and Slowed+Reverb toggle |
| **Home Screen Widget** | Android app-widget displaying the current song |
| **Splash Screen** | AndroidX Splash Screen API |
| **Permission Screen** | Friendly onboarding screen if storage/notification permissions are not yet granted |

---

## Screenshots

> _Add screenshots here once the app is running on a device or emulator._

---

## Architecture

SnoahTune follows **MVVM with a Clean-Architecture-lite layering**:

```
┌───────────────────────────────────┐
│           UI Layer                │
│  Compose Screens + NavGraph       │
│  ViewModels (HomeViewModel,       │
│              PlayerViewModel)     │
└────────────────┬──────────────────┘
                 │ observes StateFlow
┌────────────────▼──────────────────┐
│         Domain Layer              │
│  MusicRepository interface        │
│  Domain models (Song, Album, …)   │
└────────────────┬──────────────────┘
                 │ implemented by
┌────────────────▼──────────────────┐
│          Data Layer               │
│  MusicRepositoryImpl              │
│  Room Database (songs, favorites, │
│    playlists)                     │
│  MediaStoreDataSource             │
└───────────────────────────────────┘
         │
┌────────▼──────────┐
│   MusicService    │  ← Media3 MediaSessionService
│   (ExoPlayer)     │
└───────────────────┘
```

Dependency injection is provided by **Hilt** (`AppModule` registers the repository, DAO, and `MediaStoreDataSource` as singletons).

---

## Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin |
| UI toolkit | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Audio playback | AndroidX Media3 — ExoPlayer + MediaSession |
| Database | Room 2.6 (Song, Favorite, Playlist entities) |
| Dependency Injection | Hilt (Dagger) + KSP |
| Image loading | Coil 2 (album art via `content://media/external/audio/albumart/`) |
| Preferences | DataStore Preferences |
| Runtime Permissions | Accompanist Permissions |
| Color palette | AndroidX Palette |
| Min / Target SDK | 24 / 35 |
| Build tooling | Gradle 8 (Kotlin DSL), KSP |

---

## Project Structure

```
app/src/main/java/com/snoahtune/app/
├── MainActivity.kt             # Entry point, permission gating
├── MusicApplication.kt         # Hilt application class
│
├── data/
│   ├── local/
│   │   ├── MediaStoreDataSource.kt   # Queries Android MediaStore
│   │   ├── MusicDatabase.kt          # Room database definition
│   │   ├── dao/                      # SongDao, FavoriteDao, PlaylistDao
│   │   └── entities/                 # Room entities & relations
│   └── repository/
│       └── MusicRepositoryImpl.kt
│
├── domain/
│   ├── model/                    # Song, Album, Artist data classes
│   └── repository/
│       └── MusicRepository.kt    # Repository interface
│
├── di/
│   └── AppModule.kt              # Hilt bindings
│
├── service/
│   └── MusicService.kt           # Media3 MediaSessionService (ExoPlayer host)
│
├── viewmodel/
│   ├── HomeViewModel.kt          # Library data, search, sort, playlists
│   └── PlayerViewModel.kt        # Playback state, Media3 controller
│
├── ui/
│   ├── components/
│   │   ├── MiniPlayer.kt         # Bottom-bar mini player
│   │   ├── SongItem.kt           # Song list row
│   │   └── NeuComponents.kt      # NeuCard, NeuButton reusables
│   ├── navigation/
│   │   └── NavGraph.kt           # Compose NavHost + bottom nav
│   ├── screens/
│   │   ├── HomeScreen.kt         # All-songs list with search & sort
│   │   ├── LibraryScreen.kt      # Favorites, Playlists, Albums, Artists tabs
│   │   ├── NowPlayingScreen.kt   # Full-screen player
│   │   └── PermissionScreen.kt   # Storage permission onboarding
│   └── theme/
│       ├── Color.kt              # Neu-Brutalism palette
│       └── Theme.kt              # MaterialTheme wrapper
│
└── widget/
    └── MusicWidgetProvider.kt    # Home screen AppWidget
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 35
- A physical or virtual device running Android 7.0 (API 24)+

### Build & Run
1. Clone the repository.
2. Open the project root in Android Studio.
3. Let Gradle sync and download dependencies.
4. Run on a device or emulator with the **app** configuration.
5. Grant the requested storage (and notification on Android 13+) permissions.
6. SnoahTune will automatically scan and display your audio files.

---

## Suggested Future Features

Below are feature ideas that would complement the existing codebase nicely.

### Playback Enhancements
- **Sleep Timer** — stop playback after a user-defined duration (e.g. 15 / 30 / 60 min)
- **Crossfade** — smoothly fade between tracks
- **Gapless Playback** — seamless transitions for live albums and DJ mixes
- **Equalizer / Tone Controls** — per-band EQ using Android's built-in `AudioEffect` API
- **Pitch Control (independent of speed)** — let users raise/lower pitch without affecting tempo, and vice-versa

### Queue Management
- **"Play Next" / "Add to Queue"** — insert songs ahead of the upcoming track
- **Queue Screen** — view, reorder, and remove songs from the current queue
- **Drag-to-reorder Queue** — long-press to drag songs within the queue

### Discovery & Organisation
- **Folder Browser** — navigate music by directory path
- **Genres** — group songs by genre tag
- **Recently Played** — automatically tracked history
- **Most Played** — ranked chart of your top tracks
- **Smart Playlists** — auto-generated playlists (e.g. "Top 25 Most Played", "Recently Added")

### Now Playing Screen
- **Lyrics Display** — show embedded lyrics (ID3 `USLT` tag) or support `.lrc` sidecar files
- **Album Colour Theming** — extract dominant colour from album art with Palette API and tint the Now Playing screen dynamically (the dependency is already included)
- **Waveform / Visualiser** — animated audio visualiser on the Now Playing screen

### Social & Sharing
- **Share Song** — share a track's file or metadata via Android sharesheet
- **Set as Ringtone** — write the selected track as device ringtone (permission is already declared in the manifest)

### App & System Integration
- **Android Auto** — `MediaBrowserServiceCompat` extension so SnoahTune appears in car displays
- **Wear OS Companion** — basic playback controls on a paired smartwatch
- **Bluetooth / Headset Shortcut Handling** — respond to headset button clicks (single-tap play/pause, double-tap skip)
- **Car Mode UI** — large-button layout optimised for in-vehicle use

### Settings & Customisation
- **Dark Mode / Theme Switcher** — alternate colour palette (dark Neu-Brutalism) stored via DataStore
- **Accent Colour Picker** — let users swap Electric Yellow for another accent from a preset palette
- **Notification Customisation** — choose which controls appear in the media notification

### Library Management
- **Tag Editor** — edit title, artist, album, and cover art stored in file metadata
- **Playlist Import / Export** — read and write M3U / M3U8 playlist files
- **Duplicate Song Detector** — flag songs that appear to be duplicates by title+artist

