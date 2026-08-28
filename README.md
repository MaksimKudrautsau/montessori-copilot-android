# Montessori Copilot — Android App

Implements design doc v0.2: native Kotlin/Compose UI, Python business logic
embedded via Chaquopy, everything local (no AI, no backend, no accounts).
See `brainstorm-v0.1.md` / `brainstorm-v0.2.md` in the project for the full
design rationale.

## Known limitation: this was built in a sandbox with no Android SDK

The environment that generated this scaffold has no Android SDK and cannot
reach `dl.google.com` / `maven.google.com` (Google's Maven repo, required
for AndroidX/Compose/Room/AGP). That means:

- **What *was* verified here**: the entire Python logic layer
  (`app/src/main/python/logic`) — 19 passing `pytest` tests, plus a sanity
  run of the recommendation engine against the real seed content across six
  age points (see the session transcript). This code is genuinely correct,
  not just plausible-looking.
- **What was *not* verified**: the Kotlin/Gradle/Compose/Room code has never
  been compiled. It was written carefully and cross-checked by hand (method
  signatures, constructor argument order, imports), but the first `./gradlew
  build` in Android Studio will likely surface a handful of small issues —
  a missing import, a dependency version bump Android Studio suggests, etc.
  Treat this as a strong first draft to compile and fix up, not
  guaranteed-working code.

**To actually build it**: open this folder in Android Studio (Otter or
newer) on a machine with normal internet access. It should offer to sync
Gradle and suggest dependency updates — accept those, since the versions in
`gradle/libs.versions.toml` were pinned from documentation, not resolved
against Maven live (see the version note at the top of that file).

## Project layout

```
app/src/main/java/com/montessoricopilot/app/
  MontessoriApp.kt, MainActivity.kt      — entry points
  data/content/                          — Room: read-only curated content
  data/user/                             — Room: read/write household data
  data/repository/                       — the seam between UI and data/logic
  logic/                                 — Kotlin<->Python bridge (Chaquopy)
  ui/theme/, ui/navigation/, ui/screens/ — Compose UI
  viewmodel/                             — one per screen

app/src/main/python/logic/               — pure-Python business logic
  recommendation.py    — rule-based activity matching (no AI)
  rotation.py           — shelf-rotation-due logic
  category_period_map.py — activity category -> sensitive period lookup
  bridge.py              — JSON-in/JSON-out entry points Kotlin calls

app/src/main/assets/content_seed.json    — the curated content library
tools/generate_content_seed.py           — script that authors it
tests/                                   — pytest suite for the Python logic
```

## Running the one thing that's fully verified

```bash
pip install pytest --break-system-packages   # or use a venv
pytest tests/ -v
```

All 19 tests should pass. To regenerate the content seed after editing
`tools/generate_content_seed.py`:

```bash
python3 tools/generate_content_seed.py
```

## Design decisions worth knowing about

- **Two Room databases, not one**: `content.db` (curated, seeded once from
  `content_seed.json` via a `RoomDatabase.Callback`, effectively read-only
  after that) and `userdata.db` (the household's actual data). This is a
  refinement from the v0.2 doc's original plan to ship `content.db` as a
  prebuilt SQLite asset via `createFromAsset()` — that approach turned out
  to conflict with Room's internal schema-hash validation for prepackaged
  databases, so seeding via callback is used instead. Functionally
  equivalent, just more robust.
- **Kotlin↔Python boundary is JSON strings, not object marshalling** (see
  `logic/PythonBridge.kt` and `logic/bridge.py`). Slightly more overhead
  than passing objects directly through Chaquopy, but it keeps the contract
  simple, debuggable, and exactly mirrors what the Python `pytest` suite
  already exercises.
- **Python calls run on `Dispatchers.Default`, never the caller's
  dispatcher.** Chaquopy calls are blocking native calls; the two
  repository methods that invoke Python (`RecommendationRepository`,
  `ShelfRepository`) explicitly `withContext(Dispatchers.Default)` around
  them so they can't block the UI thread.
- **Repository pattern is the seam for "wider" later.** You mentioned
  wanting accounts and broader functionality eventually — every screen
  talks only to a repository interface-shaped class (`ChildRepository`,
  `ContentRepository`, etc.), never directly to Room or Python. Adding
  accounts/sync later means introducing a remote data source behind these
  same repository classes, not rewriting the UI or ViewModels.
- **No launcher icon artwork** — `ic_launcher_background/foreground.xml` are
  functional placeholders (flat color + a simple leaf mark) so the project
  builds; swap them before shipping.
- **Manual DI, no Hilt** — `ViewModelFactory` is a small hand-rolled lambda
  factory. Fine at this size; worth reconsidering if the dependency graph
  grows once a backend is added.

## What's genuinely done vs. still open

Done: full data model, working (tested) recommendation + rotation logic, a
24-activity / 9-sensitive-period starter content library across 0–6 years,
and a complete navigation flow (child picker → Today / Library / Journal /
Shelf).

Still open, in rough priority order:
1. Compile it in Android Studio and fix whatever the first build surfaces.
2. Expand `tools/generate_content_seed.py` — 24 activities is a starting
   skeleton, not a real content library.
3. `DatePicker` for "add child" instead of the placeholder year/month text
   fields.
4. `WorkManager` job + local notification for shelf-rotation reminders
   (design doc v0.2 §2 describes this; not yet wired up).
5. Compose UI tests / Room instrumented tests (see design doc v0.2 §3 for
   the intended test strategy per layer).
