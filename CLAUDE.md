# CLAUDE.md

Guidance for Claude Code (and other AI assistants) working in this repository.

## Project overview

LT Companion is an **unofficial Android app for [Language Transfer](https://www.languagetransfer.org/)**,
an audio-based language course. The app downloads the audio course (currently Spanish only),
plays lessons, and auto-pauses at "pausepoints" so the learner has time to think of the answer
before the recording continues.

- Package: `com.lukeneedham.languagetransfer`
- Application ID: `com.lukeneedham.languagetransfer` (debug builds use suffix `.dbg`)
- Min SDK 23, Target/Compile SDK 36
- 100% Kotlin, Jetpack Compose UI

## Tech stack

- **UI**: Jetpack Compose + Material3, custom theme (`ui/theme`)
- **DI**: Koin (`di/KoinModules.kt`)
- **Navigation**: Navigation 3 (`androidx.navigation3`): `AppNavigation` owns the back stack and `NavDisplay`, `Router` is the exhaustive `when` over the sealed `Route` (`ui/navigation`; see `docs/app-shell.md` in `LukeNeedham/ci-workflows`)
- **Networking**: Ktor client (downloading the course zip)
- **Database**: Room (`data/persistence`)
- **Preferences**: AndroxX DataStore (`data/persistence/prefs`)
- **Audio playback**: Media3/ExoPlayer with a foreground `MediaSessionService` (`ui/player`)
- **Serialization**: kotlinx.serialization
- **Other**: Accompanist (system UI controller), Konfetti (celebration animation), kotlin-parcelize

## Build & development

This is a standard Gradle Android project (single `:app` module).

```bash
./gradlew assembleDebug      # build debug APK
./gradlew test                # JVM unit tests (app/src/test)
./gradlew connectedAndroidTest # instrumented tests, requires emulator/device (app/src/androidTest)
./gradlew lint                 # Android lint
```

There is no ktlint/detekt config — follow the existing code style (see Conventions below).

A debug keystore (`debug.keystore`) is committed at the repo root so debug builds are
reproducible/signed consistently; the `debug` build type uses applicationIdSuffix `.dbg` so it
can be installed alongside a release build.

### CI

`.github/workflows/trigger_on_pull_request.yml` runs on PRs targeting `main`:
- Builds `assembleDebug`
- Creates a GitHub Release tagged with the branch/run info and uploads the debug APK
- Posts a sticky PR comment with a download link to the APK

## High-level architecture

The app follows a fairly conventional layered structure under
`app/src/main/java/com/lukeneedham/languagetransfer/`:

```
data/
  network/        - Ktor file downloading, course download repository
  persistence/    - Room database, DAOs, entities, DataStore-backed prefs DAOs
  repository/     - Repositories combining data sources for domain use
domain/
  model/          - Core domain models (CourseLesson, LanguageCourse, ...)
  pausepointreport/ - Logic for reporting/adjusting pausepoint accuracy
di/
  KoinModules.kt  - All Koin module definitions (single source of DI wiring)
ui/
  feature/        - One package per screen/feature (home, lesson, startup,
                    downloadlanguage, debug), each typically with
                    Page (Composable), ViewModel, and State
  navigation/     - Route (sealed nav destinations) + Router (NavHost)
  player/         - Audio playback abstraction (AudioPlayer, PlaybackRepository,
                    AudioMediaService for the media notification)
  theme/          - Compose theme, colors, typography
  util/           - General UI utilities (formatters, modifiers, sound effects,
                    color generation)
util/
  AppResult.kt    - sealed Success/Failure result type used across repositories
  EventChannel.kt / EventDataChannel.kt - one-shot event flows for ViewModels
  DebugOptions.kt - centralized debug flags backed by DataStore prefs
```

### Data flow / course loading

1. `LanguageDownloadRepository` downloads and unzips the Spanish course audio into app storage
   (`isSpanishCourseDownloaded()` / `downloadSpanishCourse()`).
2. `PausePointsRepository` and `LessonNamesRepository` read bundled JSON from
   `app/src/main/assets/pausepoints/spanish.json` and `assets/lessonnames/spanish.json`.
3. `AudioLessonRepository.getLanguageCourse()` combines downloaded audio files with pausepoints
   and lesson names into a `LanguageCourse` of `CourseLesson`s, caching the result. Currently
   hardcoded to `"spanish"`.
4. `CompletedLessonRepository` (Room-backed) tracks which lessons the user has completed.

### Navigation

- `Route` (`ui/navigation/Route.kt`) is a `@Parcelize sealed interface` with one member per screen, so `Route.Home` is
  `HomePage`: `Route.Startup`, `Route.CourseDownload`, `Route.Home`, `Route.Lesson(lesson: CourseLesson)`, `Route.Settings`,
  `Route.AppDebug`, plus the library's `Route.Debug`, `Route.Logs` and `Route.Crashes`.
- `Router` (`ui/navigation/Router.kt`) hosts an `AnimatedNavHost` and maps each route to its
  Composable, wiring up navigation callbacks (`onLessonClick`, `onBack`, etc.).
- Add a new screen by: adding a member of `Route`, a feature package under `ui/feature/` with an `XPage` (gets the ViewModel)
  and a stateless `internal XPageContent`, each in its own file with its `XViewModel` and `XUiState`, wiring it into `Router`,
  and registering its ViewModel in `KoinModules`.

### DI (Koin)

All DI wiring lives in `di/KoinModules.kt`, grouped into modules (`domain`, `player`, `sfx`,
`debug`, `network`, `database`, `prefs`, `repository`, `viewModels`) and combined into
`KoinModules.modules`, started in `App.onCreate()`. When adding a new class that needs
injection:
- Add a `single { ... }` (singleton) or `factory { ... }` (new instance per request) binding in
  the appropriate module.
- ViewModels are registered with `viewModel { ... }` in the `viewModels` module and obtained in
  Composables via `koinViewModel()`.
- `LessonSpecificViewModelFactory` is a manual factory (not a Koin `viewModel`) because it needs
  to create a new `LessonSpecificViewModel` per lesson with a custom `CoroutineScope`.

### ViewModel / state conventions

- Each feature has a `XxxState` sealed class (commonly `Loading` / `Success` / `Error` variants)
  exposed from the ViewModel as `var uiState by mutableStateOf<XxxState>(...)`.
- ViewModels recompute state via a `calculateUiState()` + `refreshUiState()` pattern after any
  underlying data changes (collected via `viewModelScope.launch { flow.collect { ... } }`).
- One-shot events (e.g. "scroll to this item", "navigate back") use `EventChannel` (no payload)
  or `EventDataChannel<T>` (with payload) rather than putting transient events in state.
- Repository calls that can fail return `AppResult<T>` (`Success`/`Failure`) instead of throwing.
- `LessonViewModel` is a good example of the "outer VM delegates to an inner, recreated VM"
  pattern used for per-lesson state (`LessonSpecificViewModel`), including using a child
  `CoroutineScope` with its own `SupervisorJob` so it can be cancelled independently when the
  lesson changes.

### Audio playback

- `AudioPlayer` / `AudioPlayerProvider` wrap Media3 ExoPlayer; `AudioMediaService` is a
  `MediaSessionService` providing the media notification (declared in `AndroidManifest.xml`).
- `PlaybackRepository` is a singleton used to signal playback resume events across ViewModels
  (e.g. continuing to the next lesson).
- `PausepointChecker` handles detecting when playback crosses a pausepoint and should auto-pause.

### Debug tooling

- `util/DebugOptions.kt` centralizes debug flags (`allLessonsCompleted`,
  `showDebugLessonControls`, `shouldAutoPause`, `allowSeekProgressBar`), backed by
  `PrefsBooleanKey` / `DebugPreferencesDao` (DataStore).
- `ui/feature/debug/AppDebugPage.kt` + `DebugViewModel` provide an in-app debug menu for toggling
  these flags and navigating directly to any `Page` (including a dummy lesson).
- `PausepointReporter` / `domain/pausepointreport` let users report inaccurate pausepoints
  (missing, unnecessary, too early/late) from within a lesson, which is recorded for later
  analysis/correction of `languagedata/pausepoints/*.json`.

## `languagedata/` (offline data pipeline, not part of the Android app)

A separate Python pipeline used to generate the bundled pausepoint/lesson-name JSON assets:

- `languagedata/process.py` processes raw Language Transfer audio (in the gitignored
  `languagedata/audiosource/<language>/`, unzipped from
  `https://downloads.languagetransfer.org/<language>/<language>.zip`) using the Gemini API to
  produce `languagedata/pausepoints/<language>.json`.
- Requires a `languagedata/local.properties` with `GEMINI_API_KEY=...` (gitignored).
- Output JSON files are the ones copied into `app/src/main/assets/pausepoints/` and
  `app/src/main/assets/lessonnames/` for the app to consume.
- See `languagedata/README.md` for full instructions.

This pipeline is independent of the Gradle build — only touch it if asked to regenerate or
adjust pausepoint/lesson-name data.

## Conventions

- KDoc-style comments (`/** ... */`) are used on most public classes/functions describing
  purpose and `@property`/`@param`/`@return` — follow this style for new public API in
  `data`/`domain`/`util`.
- Sealed classes/interfaces for state and results (`HomeUiState`, `LessonUiState`, `AppResult`,
  `Route`, `PrefsBooleanKey`, etc.) rather than booleans/enums with implicit meaning.
- Repository constructors take their dependencies (other repositories/DAOs), never `Context`
  directly except where unavoidable (e.g. `LanguageDownloadRepository`, `FileDownloader`,
  `PausePointsRepository` use `androidContext()` from Koin for assets/files dir).
- Feature packages under `ui/feature/<name>/` contain `<Name>Page.kt` (Composable),
  `<Name>ViewModel.kt`, and `<Name>State.kt`; larger features add `component/`, `state/`, or
  `model/` sub-packages (see `ui/feature/lesson/`, `ui/feature/home/`).
- `Millis` (`util/model/Millis.kt`, a `Long` typealias) is used throughout for durations/positions
  instead of raw `Long`, for clarity.

<!-- ci-workflows:begin (managed by LukeNeedham/ci-workflows, do not edit) -->
- `.claude/skills/lukeneedham-conventions` and the callers `trigger_on_pull_request.yml`, `build_main.yml`, `delete_prereleases.yml`, `release.yml` in `.github/workflows/` are kept in sync from
  `LukeNeedham/ci-workflows` by CI, which keeps one PR titled "Sync from ci-workflows" open when they
  differ. Do not edit them here and do not copy them by hand: change them in ci-workflows. The callers
  hold no settings: every app is built the same way (see `docs/app-config.md` there).
- **Always give the user the AppHub link when work on this app completes.** As soon as a PR is open,
  paste `📦 [Open in AppHub](https://www.lukeneedham.com/apphub/pr/<repo name>/<PR number>)` into the chat
  on a line of its own, never inside a sentence (CI posts the same link as a PR comment), and put the same
  line at the very top of the PR description. Do not wait for the build and do not paste the APK link instead.
- **`README.md` is generated by CI from `readme.yml`**: never edit `README.md`, edit `readme.yml` (write it once
  from `docs/readme-template.yml` in `LukeNeedham/ci-workflows` if it is missing) and keep it accurate. Every app
  has a `LICENSE` file. The skill has the details.
- **Every page is an `XPage` plus a stateless `internal` `XPageContent`, always** (`XPage` gets its
  `XViewModel`; its route is always `Route.X`, a member of the one `sealed interface Route`; each of `XPage`,
  `XPageContent`, `XViewModel` and `XUiState` is in its own file; never `XScreen`). The README screenshots are private
  `@Preview`s in the package `screenshot` (in `src/debug`, or `src/androidDebug` in a Kotlin Multiplatform app), each owning its mock data (a page can have several,
  or none; nothing is shared); CI renders them into `docs/screenshots/`, never by hand. The skill has the details.
- **The Stop hook's git warnings** ("unpushed commit(s)", or commits "Unverified" with a `github-actions[bot]` email):
  run `git fetch origin` and check again, and do nothing else. CI pushes commits of its own to a PR branch after you
  push (the README, the screenshots), so your local copy of the remote branch is out of date and Git thinks commits are local only. Never amend,
  rebase or force-push to silence the hook, never edit the hook, and do not write about it at length.
<!-- ci-workflows:end -->
