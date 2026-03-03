# Changelog

All notable changes to SnoahTune are listed here in reverse chronological order.

---

## [Unreleased] — 2026-02-25 to 2026-02-26

Features and improvements added after the playlist bug fix.

### New Features

- **Slowed + Reverb Mode** — Toggle on the Now Playing screen to enable pitch-linked playback speed. When active, lowering the speed also lowers the pitch, producing a dreamy slowed-reverb aesthetic. Integrated with the existing speed selector so the effect is always in sync.

- **System Equalizer Integration** — The EQ button on the Now Playing screen now launches the device's built-in audio effect control panel (`AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL`). Gracefully does nothing on devices that do not ship a system EQ.

- **Share Song** — A *Share Song* option in the Now Playing ⋮ menu fires a standard `ACTION_SEND` intent, letting users share the current song's title and artist to any app (messaging, social media, etc.).

- **Playback Speed Control** — A speed button on the Now Playing action row opens a bottom sheet with seven preset speeds: 0.5×, 0.75×, 1.0×, 1.25×, 1.5×, 1.75×, and 2.0×. The active preset is highlighted and the button label updates live.

- **Swipe Gestures on Album Art** — A horizontal drag on the album art in the Now Playing screen skips to the next song (swipe left) or the previous song (swipe right), using an 80 px threshold to avoid accidental skips.

- **Real-time Song Search** — A search bar at the top of the Home screen filters the song list instantly by title, artist, or album name. A clear (✕) button appears when text is present.

- **Advanced Sort Options** — Seven sort modes are available from a *SORT* button on the Home screen: Date Added (Newest), Date Added (Oldest), Name A–Z, Name Z–A, Duration (Longest), Duration (Shortest), and Artist Name.

- **Albums Tab in Library** — A dedicated *ALBUMS* tab in the Library screen lists every album with its artist and track count. Tapping an album immediately queues and plays its songs.

- **Fully Functional Now Playing ⋮ Menu** — The three-dot menu on the Now Playing screen now contains working actions: *Add to Favorites*, *Share Song*, and *Song Info*.

- **Add to Playlist from Home Screen** — The song options sheet (⋮ menu on any song in the Home screen) now includes an *Add to Playlist* action. Users can pick an existing playlist or create a brand-new one inline without leaving the current screen.

- **Home Screen Widget** — A persistent Android App Widget displays the current song title and artist on the device home screen, with a play/pause indicator that reflects live playback state. Tapping the widget opens the app.

### Improvements

- **Playlist Management** — Playlists can now be deleted directly from the Library screen via a delete icon on each playlist card.

- **LibraryScreen Refactoring** — Internal code restructured for improved readability; visual output is unchanged.

- **Widget Refinements** — Widget layout dimensions, text styles, and `AppWidgetProviderInfo` configuration parameters were iteratively updated for a polished, consistent appearance.

---

## [Initial Release] — 2026-02-25

- Project scaffolded: Jetpack Compose, Media3 ExoPlayer, Room database, Hilt DI.
- Core screens: Home, Now Playing, Library (Favorites + Playlists tabs), Permission.
- Mini-player persistent at the bottom of all screens.
- Playlist create/add-song workflow (buggy in first pass — fixed in follow-up commits above).
- GitHub Actions CI workflow (`build.yml`).
