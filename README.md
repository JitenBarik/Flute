# 🎵 Flute — Clean, Ad-Free Music App for Android

**Flute** is a modern, lightweight, and completely ad-free music listening app inspired by Spotify and YouTube Music. It runs fast, uses almost no battery, and supports older Android devices (Android 5.0+ / API 21 through Android 14+).

---

## 🌟 Key Highlights

- 🚫 **100% Ad-Free**: No video ads, banner ads, or audio interruptions.
- 🔓 **No Account Required**: Ready to play immediately after install.
- 📱 **Old Phone Support**: Compatible with Android 5.0 (Lollipop) all the way up to modern Android versions.
- 🎶 **Dual Music Sources**:
  - **Local Storage**: Automatically scans all MP3, FLAC, WAV, AAC, and OGG files saved on your phone.
  - **Jamendo Streaming**: Free and legal online streaming of over 600,000+ indie songs licensed under Creative Commons.
- 🧠 **YouTube Music Features**:
  - *Listen Again*: Fast access to your recently played tracks.
  - *Quick Picks*: Smart rotation of tracks from your device library.
  - *Smart Mixes*: Discover tracks by genre (Pop, Rock, Hip-Hop, Lo-Fi, Electronic, Ambient, etc.).
- 🎧 **Spotify-Style Audio Player**:
  - Bottom mini-player with live progress bar.
  - Full-screen expanding player with album art, scrub bar, shuffle, repeat, and like controls.
  - Background playback service with lock screen and notification media controls.
  - Sleep timer to automatically stop playback when you fall asleep.

---

## 🛠️ Architecture & Tech Stack

- **UI**: Jetpack Compose + Material 3 Design
- **Language**: Kotlin 1.9 (JVM 17)
- **Audio Engine**: AndroidX Media3 (ExoPlayer 1.2.1) + MediaSession
- **Local DB**: Room Database (Playlists, Liked Songs, History)
- **Networking**: Retrofit 2 + OkHttp + Gson
- **Image Loader**: Coil Compose
- **Architecture**: MVVM + Clean Repository Pattern (with lightweight Service Locator DI)

---

## 📂 Project Structure

```
E:\Flute\
├── app/
│   ├── src/main/
│   │   ├── java/com/flute/music/
│   │   │   ├── FluteApp.kt              # Application class & DI root
│   │   │   ├── MainActivity.kt          # Main activity, permissions & bottom bar
│   │   │   ├── di/                      # AppContainer (lazy singleton DI)
│   │   │   ├── data/
│   │   │   │   ├── api/                 # Jamendo API Retrofit service
│   │   │   │   ├── db/                  # Room Database, DAOs, Entities
│   │   │   │   ├── model/               # Song, Album, Artist, Playlist models
│   │   │   │   └── repository/          # Local, Online, Playlist, Liked, Recent
│   │   │   ├── player/                  # MusicPlayerManager & Media3 PlaybackService
│   │   │   └── ui/
│   │   │       ├── components/          # MiniPlayer, SongListItem, SongCard, EmptyState
│   │   │       ├── home/                # Home screen, Listen Again, Quick Picks
│   │   │       ├── search/              # Search local & online, genre chips
│   │   │       ├── library/             # Liked Songs, Playlists, Device Songs
│   │   │       ├── player/              # Full-screen player with animations
│   │   │       ├── settings/            # Sleep timer, equalizer, theme
│   │   │       ├── navigation/          # Screen routes & NavHost
│   │   │       └── theme/               # Dark OLED color palette & typography
│   │   ├── res/                         # Strings, colors, themes, adaptive icon
│   │   └── AndroidManifest.xml          # Permissions & MediaSessionService declaration
├── build.gradle.kts                     # Root Gradle config
├── settings.gradle.kts                  # Project settings
└── gradle.properties                   # JVM & AndroidX settings
```

---

## 🚀 How to Run

1. Open **Android Studio** (Hedgehog or newer recommended).
2. Click **File > Open** and choose `E:\Flute`.
3. Allow Gradle to sync the dependencies.
4. Connect an Android phone (via USB debugging) or start an Android Emulator.
5. Click the green **Run (▶)** button!
