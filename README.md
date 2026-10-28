# 📝 Notora - Smart Offline Note-Taking & Rich Text Productivity Suite

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android Gradle Plugin](https://img.shields.io/badge/AGP-9.4.1-blue.svg?style=flat&logo=android)](https://developer.android.com/studio/releases/gradle-plugin)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.09.00-green.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Expressive-orange.svg?style=flat&logo=materialdesign)](https://m3.material.io/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM%20%2B%20UDF-brightgreen.svg?style=flat)](#-architecture--design-patterns)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

**Notora** is an offline-first, native Android note-taking and rich-text productivity application. Built with **Kotlin**, **Jetpack Compose (Material 3)**, and strict **Clean Architecture**, Notora empowers users to capture thoughts, format rich text notes, organize collections with categories and tags, browse timelines, manage note revisions, set time-critical reminders, and export backups—all with zero cloud tracking and no account registration.

---

## 🌟 Overview & Philosophy

Notora operates on three core principles:
1. **100% Offline & Privacy-First**: All personal notes, rich text documents, checklists, attachments, and backups stay strictly on your local device. Zero tracking, zero telemetry, zero cloud dependencies.
2. **Seamless Note-Taking Flow**: From lightning-fast quick note creation to robust rich text editing and interactive checklists, every workflow is designed for frictionless interaction.
3. **Engineering Excellence**: Built with strict Clean Architecture, state hoisting, responsive dual-orientation layouts, Room FTS4 full-text search indexing, and KotlinX Serialization.

---

## ✨ Core Features & Capabilities

### 📝 1. Rich Text Editor & Interactive Checklists
- Full formatting suite supporting **Bold**, *Italic*, <u>Underline</u>, <s>Strikethrough</s>, Headings (H1, H2, H3), Inline Code, Code Blocks, Blockquotes, Bullet Lists, and Numbered Lists.
- **Custom Text & Highlight Colors**: Pick custom color accents for specific text selections.
- **Web Links**: Insert clickable hyperlinks with automatic URL validation and annotations.
- **Interactive Checklists**: Visual checklist progress tracking with completion percentages, strikethrough styling, and one-tap toggling.
- **Undo / Redo History Stack**: Built-in 50-step undo and redo history for fearless note editing.

### 🎨 2. Theme-Aware Note Backgrounds & Color Palette
- Assign custom color themes to individual notes (Paper White, Soft Amber, Mint Green, Lavender, Sky Blue, Rose, Dark Slate, etc.).
- Automatic light and dark paper surface switching with intelligent backward-compatibility RGB distance mapping.

### 🛡️ 3. Debounced Touch Guard & Navigation
- Built-in 500ms click guard architecture (`clickableDebounced` & `rememberDebouncedClick`) across all touch targets, FABs, and buttons to eliminate rapid multi-click/double-tap glitches.

### ⚡ 4. Zero-Latency Room FTS4 Full-Text Search
- Instant global full-text search powered by SQLite Room **FTS4 virtual tables** (`notes_fts`) indexing both titles and plain text contents.
- Filter by category, tag, note type, color, and pin/favorite status.

### 📐 5. Adaptive & Responsive Layouts
- Fluid responsive UI supporting both Portrait vertical flows and Dual-Column Landscape layouts with a cradled bottom navigation bar and floating center speed-dial FAB.

### 🏷️ 6. Categorization, Tagging, Pins & Favorites
- Organize notes effortlessly using custom colored categories and tags.
- Pin critical notes to the top or mark notes as favorites for immediate access.

### 📅 7. Calendar Timeline View
- Visual calendar timeline to browse notes by creation date, modification date, or scheduled reminders.

### 🕒 8. Note Revisions & Versioning History
- Automatic revision snapshot generation during significant edits. Track edit history and restore previous versions of any note in one tap.

### 📋 9. Pre-Made Note Templates
- Pre-configured templates for quick and structured note creation (Meeting Notes, Study Guides, Journals, Checklists, Code Snippets).

### ⏰ 10. Reminders & Notifications
- Set time-sensitive reminders with reliable background alarms (`AlarmManager` & boot receiver support).

### 📦 11. Archive & Trash Management
- Clean up your workspace by archiving notes or moving deleted items to Trash with full restore/delete capabilities and a secure empty trash function.

### 💾 12. Strict Schema-Validated JSON Backup & Export
- Full application backup and restore coordinates persistent storage JSON export and import using **KotlinX Serialization**.
- Single-note file export supporting TXT, Markdown, HTML, and JSON formats.

---

## 📐 Architecture & Design Patterns

Notora follows modern Android architectural standards with strict **Clean Architecture**, **MVVM**, and **Unidirectional Data Flow (UDF)**:


### Complete Project Directory & Package Structure

```
com.example.notora/
├── core/
│   ├── designsystem/
│   │   ├── components/            # Centralized Design System primitives (AppButton, AppCard, AppTopBar, AppIconButton, AppDialog, AppBottomSheet, AppDivider, AppEmptyState, AppFilterChip, AppProgress, AppSearchField, AppSwitch, AppText, AppTextField, NoteCard, ChecklistComponent, RichTextToolbar, ColorPickerBottomSheet, FilterChipBar, toolbar/*)
│   │   └── theme/                 # Theme, Color, AppTypography, AppDimensions, NoteColorPalette
│   └── utils/                     # Kotlin extensions & utilities (AppUtils, ColorUtils, DateTimeUtils, StringUtils, UiUtils, ValidationUtils)
│
├── data/
│   ├── backup/                    # Database JSON Backup/Restore manager & note file exporters
│   ├── datastore/                 # Jetpack DataStore preferences datasource
│   ├── file/                      # Attachment storage manager
│   ├── local/                     # Room Database, DAOs, Entities, FTS4, and Relations
│   │   ├── dao/                   # NoteDao, CategoryDao, TagDao, ChecklistDao, AttachmentDao, RevisionDao, TemplateDao
│   │   ├── entity/                # NoteEntity, NoteFtsEntity, CategoryEntity, TagEntity, ChecklistItemEntity, NoteLinkEntity, NoteRevisionEntity, NoteTagCrossRef, NoteTemplateEntity, TagEntity
│   │   └── relation/              # NoteWithDetails
│   └── repository/                # Concrete repository implementations (CategoryRepositoryImpl, NoteRepositoryImpl, SettingsRepositoryImpl, TagRepositoryImpl, TemplateRepositoryImpl)
│
├── di/                            # Dagger Hilt Modules
│   ├── AppModule.kt               # Application scope bindings & coroutine dispatchers
│   ├── DatabaseModule.kt          # Room database, DAOs, & WAL journal mode providers
│   └── RepositoryModule.kt        # Interface binding modules (@Binds)
│
├── domain/
│   ├── editor/                    # Rich text document model, span types, serializer, & state
│   ├── model/                     # Domain models (Note, Category, Tag, ChecklistItem, Attachment, NoteRevision, NoteDateGroup, NoteFilter, NoteSortOption, NoteTemplate, NoteType)
│   ├── repository/                # Abstract repository interfaces
│   └── usecase/                   # Single-responsibility business use cases
│       ├── backup/                # Export/Import backup use cases
│       ├── category/              # Category management use cases
│       ├── note/                  # GetActiveNotes, GetNoteById, SaveNote, SearchNotes, NoteActionUseCases, etc.
│       ├── revision/              # Note revision history use cases
│       ├── settings/              # App preferences use cases
│       ├── tag/                   # Tag management use cases
│       └── template/              # Template management use cases
│
├── presentation/                  # Presentation Layer (Jetpack Compose + MVVM + UDF)
│   ├── MainViewModel.kt           # Root activity ViewModel
│   ├── archive/                   # Archive Screen & ViewModel
│   ├── calendar/                  # Calendar Screen, CalendarDateHeader component, & ViewModel
│   ├── categories/                # Categories Screen, CategoryCardItem component, & ViewModel
│   ├── editor/                    # Rich Text Note Editor Screen, ViewModel, Visual Transformation, & components (EditorAttachmentsSection, EditorMetaChips, EditorTopBar)
│   ├── home/                      # Home Screen, HomeDrawerContent, HomeNotesGrid, HomeSearchBar, HomeSpeedDialFab, & ViewModel
│   ├── navigation/                # NavGraph, AppBottomNavBar, AppNavigationRail, & Screen routes
│   ├── revisions/                 # Note Revisions Version History Screen & ViewModel
│   ├── search/                    # Global Search Screen & ViewModel
│   ├── settings/                  # Settings Screen, LayoutPillSelector, ThemePillSelector, Cards (About, Appearance, Backup, Editor, Group, SectionHeader), & ViewModel
│   ├── splash/                    # SplashScreen & ViewModel
│   ├── tags/                      # Tags Screen, TagCardItem component, & ViewModel
│   ├── templates/                 # Templates Screen & ViewModel
│   └── trash/                     # Trash Screen & ViewModel
│
├── MainActivity.kt                # Single Activity entry point & edge-to-edge / theme host
└── NotoraApplication.kt           # Application entry point (@HiltAndroidApp)
```

### Key Engineering Practices:
- **Unidirectional Data Flow (UDF)**: ViewModels expose immutable `StateFlow<UiState>` collected with `collectAsStateWithLifecycle()`.
- **Debounced Interactions**: User actions use a 500ms debounce guard (`clickableDebounced` / `rememberDebouncedClick`) to prevent duplicate triggers.
- **Strict Design System Adherence**: 100% of screens, components, dialogs, and bottom sheets leverage centralized app common components (`AppTopBar`, `AppIconButton`, `AppCard`, `AppDialog`, `AppBottomSheet`, etc.) for consistency.
- **Database Safety & Migration Resilience**: Foreign keys enabled, WAL journal mode enabled, and explicit decision to avoid destructive migrations to guarantee zero data loss. Room FTS4 virtual table indexing enables instant search queries.
- **Code Hygiene**: Regular dead-code analysis and removal of unused utility functions to maintain a lean codebase.

---

## 🛠 Tech Stack & Dependencies

All dependencies are centrally managed via Gradle Version Catalog ([`libs.versions.toml`](gradle/libs.versions.toml)):

| Category | Library / Dependency | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) | `2.4.21` | Modern, expressive, type-safe programming |
| **Build System** | [Android Gradle Plugin (AGP)](https://developer.android.com/build) | `9.4.1` | Project build configuration |
| **Annotation Processing** | [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html) | `2.3.12` | High-performance code generation for Room & Hilt |
| **UI Framework** | [Jetpack Compose BOM](https://developer.android.com/develop/ui/compose/bom) | `2026.09.00` | Declarative UI toolkit |
| **Design System** | [Material 3](https://developer.android.com/develop/ui/compose/components) | BOM Managed | Expressive Material Design components |
| **Icons** | Material Icons Extended | BOM Managed | Comprehensive icon set |
| **Dependency Injection** | [Dagger Hilt](https://dagger.dev/hilt/) | `2.60.1` | Dependency injection framework |
| **Hilt Navigation** | Hilt Navigation Compose | `1.4.0` | ViewModel injection in Navigation destinations |
| **Navigation** | [Navigation Compose](https://developer.android.com/guide/navigation) | `2.10.2` | Single-activity Compose navigation |
| **Local Database** | [Room Database (Runtime + KTX)](https://developer.android.com/training/data-storage/room) | `2.8.5` + FTS4 | SQLite object mapping with Flow support & FTS |
| **Preferences** | [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) | `1.3.0` | Asynchronous key-value storage |
| **Serialization** | [KotlinX Serialization](https://github.com/Kotlin/kotlinx.serialization) | `1.12.0` | Reflection-free JSON backup and rich-text parsing |
| **Responsive Units** | [SDP & SSP Android](https://github.com/intuit/sdp) | `1.1.1` | Scalable size and font dimensions |
| **Image Loading** | [Coil Compose](https://coil-kt.github.io/coil/) | `2.7.0` | Asynchronous image loading |
| **Release Optimization** | R8 Shrinking & ProGuard | Enabled | Minification & resource optimization (`isMinifyEnabled = true`) |

---

## 🚀 Getting Started & Git Instructions

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2+) or newer.
- **Java Development Kit (JDK)**: JDK 17, JDK 21, or JDK 25.
- **Android SDK Requirements**:
  - `compileSdk`: `37`
  - `targetSdk`: `37`
  - `minSdk`: `26` (Android 8.0 Oreo or higher)

### 1. Clone the Repository
```bash
git clone https://github.com/hammad-ahmed-android-engineer/Notora.git
cd Notora
```

### 2. Open Project in Android Studio
1. Launch Android Studio.
2. Select **File > Open...** and navigate to the cloned `Notora` directory.
3. Allow Gradle to synchronize dependencies.

### 3. Build & Run via Command Line

#### Compile Kotlin Sources:
```bash
# Windows
.\gradlew.bat compileDebugKotlin

# macOS / Linux
./gradlew compileDebugKotlin
```

#### Assemble Debug APK:
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
The output APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### Assemble Optimized Release APK:
```bash
# Windows
.\gradlew.bat assembleRelease

# macOS / Linux
./gradlew assembleRelease
```

#### Install Directly onto a Connected Device / Emulator:
```bash
# Windows
.\gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```

---

## 🔒 Permissions & Security

Notora requests minimal local permissions required for alarms and notifications:

- `android.permission.POST_NOTIFICATIONS`: Prompts for notification permission on Android 13+ (API 33+).
- `android.permission.SCHEDULE_EXACT_ALARM`: Ensures exact-time alarms for scheduled reminders.
- `android.permission.RECEIVE_BOOT_COMPLETED`: Automatically reschedules reminders after device reboots.
- `android.permission.VIBRATE`: Provides haptic feedback during alarms.

> [!IMPORTANT]
> Notora requires **NO internet permission** (`android.permission.INTERNET`). Your notes, rich text documents, checklists, attachments, and backups remain strictly on your physical device.

---

## 🤝 Contributing

Contributions, feedback, and issue reports are welcome!
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m "Add amazing feature"`).
4. Push to the branch (`git push origin feature/amazing-feature`).
5. Open a Pull Request.

---

## 📄 License

This project is open-source and distributed under the terms of the [Apache License 2.0](LICENSE).
