# Universal Game Bot — Development Status

## Implemented in source

- Native Android app built with Kotlin and Jetpack Compose, navigation and catalogue for 25 game names.
- GitHub Actions configures Java 17, Android SDK 34 and Gradle 8.6, then runs unit tests only.
- Screen-capture flow requests Android consent before observation. The capture manager registers a MediaProjection callback, validates display dimensions, and releases virtual-display/image resources.
- Laboratory diagnostics read current screen-capture, accessibility and observation state. Unimplemented features are reported as warnings instead of being labelled ready.
- Accessibility gesture methods report whether Android accepted the dispatch; ActionEngine no longer reports success when dispatch was rejected.
- Safety is fail-closed: a GREEN confidence state alone cannot authorize input. The gate also requires a matching complete certificate for the exact game/profile version. Starting/stopping observation resets the gate.
- Game profiles begin at zero learning score and unverified. Ordinary score updates cannot certify autonomy.
- Certificate checks require matching profile/version, all tests passing, zero failures and full confidence.
- No APK has been created or compiled, following the user's instruction not to create/compile one without explicit permission.

## Important limitations — not release-ready as a universal player

- The vision engine samples bitmap metadata only and reports confidence 0.0. It does not recognize game objects, boards, buttons or state.
- The detector deliberately returns `Unknown Game` because validated visual fingerprints are absent.
- The observation loop is not connected to a functional, certified gameplay adapter.
- Added a deterministic breadth-first-search grid path solver with unit tests. It accepts a recognized `# . S G` grid, validates malformed input, returns a shortest path including endpoints, and handles unreachable goals.
- Amaze GO remains observation-only: the solver is not connected to a visual grid recognizer, path execution, or victory/failure detection, so it does not yet make the game playable autonomously.
- Sudoku, EA SPORTS FC Mobile and the other catalogue entries are not autonomous players. Listing a game does not mean it is supported.
- Learning screen controls are not connected to a real gameplay learning pipeline; scores must not be interpreted as measured capability.
- Cloud AI provider routing is not configured. The app does not currently offer automatic selection among external AI services.
- No physical-device, UI, permission-revocation or real-game tests have been performed.

## Validation

- Last earlier successful CI run: https://github.com/pippo2801/crea-bot/actions/runs/38140354922
- Current code changes must pass a fresh CI run before they are considered validated.
- CI does not compile or publish an APK. A successful unit-test run is not proof of runtime correctness.

## Release gates

1. Pass CI for the latest commit.
2. Add and test an actual game-specific recognizer and solver, initially for one supported game rather than claiming universal support.
3. Add integration tests for screen capture, gesture dispatch, permission denial/revocation and stop behavior.
4. After explicit authorization, build an APK and test on the user's phone.
5. Certify a game only after repeatable success/failure tests on real game screens.
