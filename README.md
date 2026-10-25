# Notora 📝

**Notora** is a feature-packed, modern Android note-taking application designed for seamless productivity, elegant organization, and high performance. Built entirely with **Jetpack Compose** and **Kotlin**, following **Clean Architecture** principles, Notora offers a rich text note editor, flexible categorization, calendar-based timeline views, note versioning, reminders, templates, and robust backup/export tools.

---

## 🌟 Key Features

- 📝 **Rich Text Editor**: Full formatting suite supporting Bold, Italic, Underline, Strikethrough, Headings (H1, H2, H3), Custom Text & Highlight Colors, Web Links, Bullet Lists, and Interactive Checklists.
- 🎨 **Theme-Aware Note Backgrounds**: Automatic light and dark paper surface switching with intelligent backward-compatibility RGB distance mapping for legacy notes.
- 🛡️ **Debounced Touch Guard**: Built-in 500ms click guard architecture (`clickableDebounced` & `rememberDebouncedClick`) across all touch targets, FABs, and buttons to eliminate rapid multi-click/double-tap glitches.
- ⚡ **Zero-Latency Performance**: Optimized for low-end hardware with background thread offloading (`Dispatchers.IO` for Room DB mapping) and debounced background GSON draft persistence.
- 📐 **Adaptive & Responsive Layouts**: Fluid responsive UI supporting both Portrait vertical flows and Dual-Column Landscape layouts with a cradled bottom navigation bar and floating center FAB.
- 🏷️ **Categorization & Tagging**: Organize notes effortlessly using custom colored categories and tags.
- 📌 **Pin & Favorite**: Keep critical notes pinned to the top or marked as favorites for immediate access.
- 📅 **Calendar Timeline**: Visual timeline view to browse notes by creation date or scheduled deadlines.
- 🕒 **Note Revisions & Versioning**: Track edit history and restore previous versions of any note.
- 📋 **Templates**: Pre-made templates for quick and structured note creation.
- ⏰ **Reminders & Notifications**: Set time-sensitive reminders with reliable background alarms (`AlarmManager` & boot receiver support).
- 🔍 **Advanced Search & Filtering**: Fast global search with filters by category, tag, note type, or date range.
- 📦 **Archive & Trash Management**: Clean up your workspace by archiving notes or moving deleted items to Trash with full restore/delete capabilities.
- 💾 **Backup, Restore & Export**: Strict schema-validated JSON backup import/export and single-note file exporting.
- 🎨 **Material 3 & Theme Support**: Modern UI with full support for Light, Dark, and System default themes.

---

## 🛠️ Tech Stack & Libraries

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: Clean Architecture + MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) (Dagger-Hilt)
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) with [KSP](https://kotlinlang.org/docs/ksp-overview.html)
- **Preferences**: [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Async & Concurrency**: Kotlin Coroutines & `StateFlow` / `Flow`
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/)
- **Responsive Dimensions**: SDP & SSP Scalable Units
- **Background Tasks**: `AlarmManager`, `BroadcastReceiver`, `WorkManager`

---

## 📐 Project Architecture & Module Structure

Notora follows **Clean Architecture** principles, enforcing strict separation of concerns across Data, Domain, and Presentation layers:

```text
app/src/main/java/com/example/notora/
│
├── common/                  # Application-wide utilities and helper classes
│   └── util/                # AppUtils, ColorUtils, DateTimeUtils, StringUtils, UiUtils, ValidationUtils
│
├── data/                    # Data Layer (Data Sources, Database, Repositories)
│   ├── backup/              # Database Backup/Restore manager & file exporters
│   ├── file/                # Attachment storage management
│   ├── local/               # Room Database, DAOs, Entities, and Relations
│   ├── reminder/            # AlarmManager scheduler, BootReceiver, ReminderReceiver
│   └── repository/          # Concrete implementations of domain repositories
│
├── di/                      # Dependency Injection (Hilt Modules)
│   ├── AppModule.kt         # Coroutine scope & application-level bindings
│   ├── DatabaseModule.kt    # Room database & DAO providers
│   └── RepositoryModule.kt  # Repository interface bindings
│
├── domain/                  # Domain Layer (Pure Business Logic & Data Models)
│   ├── editor/              # Rich text document model, span types, serializer, and state
│   ├── model/               # Note, Category, Tag, ChecklistItem, Attachment, etc.
│   ├── repository/          # Domain repository interfaces
│   ├── usecase/             # Feature use cases (category/, note/, revision/, tag/, template/)
│   └── util/                # Date grouper and domain helper utilities
│
├── ui/                      # Presentation Layer (Jetpack Compose UI & ViewModels)
│   ├── archive/             # Archive Screen & ViewModel
│   ├── calendar/            # Calendar Screen, Header, & ViewModel
│   ├── categories/          # Categories management Screen & ViewModel
│   ├── components/          # Reusable UI components (common/, toolbar/, NoteCard, RichTextToolbar, etc.)
│   ├── editor/              # Rich Text Note Editor Screen, ViewModel, and Visual Transformations
│   ├── home/                # Home Screen, Drawer, Notes Grid, and Speed Dial FAB
│   ├── navigation/          # NavGraph, Screen destinations, and Cradled Bottom Bar / Rail
│   ├── revisions/           # Note Version History Screen & ViewModel
│   ├── search/              # Global Search Screen & ViewModel
│   ├── settings/            # App Preferences, Appearance, & Backup Cards
│   ├── splash/              # Splash Screen
│   ├── tags/                # Tags management Screen & ViewModel
│   ├── templates/           # Templates selection/creation Screen & ViewModel
│   ├── theme/               # Material 3 Theme, Colors, Typography, Dimensions, ColorPalette
│   └── trash/               # Trash Screen & ViewModel
│
├── MainActivity.kt          # Single Activity entry point & theme initialization
└── NotoraApplication.kt    # Application class annotated with @HiltAndroidApp
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: Java 17 or higher
- **Android SDK**: Minimum SDK Level 26 (Android 8.0), Target SDK Level 37

### Building and Running

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/hammad-ahmed-android-engineer/Notora.git
   cd Notora
   ```

2. **Open in Android Studio**:
   - Open Android Studio and select **Open**.
   - Navigate to the cloned `Notora` project directory.

3. **Sync Gradle**:
   - Allow Android Studio to sync Gradle dependencies automatically.

4. **Run the Application**:
   - Connect an Android device or launch an Emulator (API 26+).
   - Press **Run (Shift + F10)**.

---

## 🧪 Testing

The project includes unit and UI testing setups:
- **Unit Tests**: Located in `app/src/test/` using JUnit 4, MockK, and Kotlinx Coroutines Test.
- **UI & Instrumentation Tests**: Located in `app/src/androidTest/` using Compose UI Test and Espresso.

Run unit tests via Gradle:
```bash
./gradlew test
```

---

## 📄 License

This project is open source and available under the [Apache License 2.0](LICENSE).
