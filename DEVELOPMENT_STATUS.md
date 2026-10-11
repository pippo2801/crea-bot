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
- Added `GridCoordinateMapper`, which converts recognized grid-cell centers to screen coordinates only when board bounds, screen dimensions, and cell indices are valid. Unit tests cover valid mapping, invalid bounds, non-finite values, and invalid paths.
- Added `GridPathPlanner` to combine shortest-path calculation with screen-coordinate planning, with explicit invalid-grid, unreachable-path, invalid-bounds, and rejected-observation outcomes plus unit tests.
- Added `GridObservationValidator`: it rejects unknown game IDs, malformed/non-rectangular grids, missing or duplicate start/goal markers, invalid screen dimensions/bounds, non-finite confidence, and confidence below 0.95. `planValidated` routes only validated observations into the planner.
- Added configurable offline `GridImageRecognizer` using measured pixel edges, regular line spacing, central cell color samples, explicit color ranges, and failure results for unsupported images, incomplete grids, ambiguous cells, and missing/non-unique start or goal. JVM tests use synthetic pixel buffers rather than Android Bitmap mocks.
- Added an optional recognizer injection point to `AmazeAdapter` and connected successful recognition to `GridPathPlanner.planValidated`. The adapter can report a validated path and screen-coordinate plan as data only; it never dispatches gestures. The default adapter has no calibrated recognizer, game detection remains false, and actions remain disabled. Color ranges and grid-line thresholds are uncalibrated hypotheses until checked against real Amaze GO screenshots.
- Extended synthetic tests to cover recognition → confidence validation → path planning and to confirm confidence below 0.95 is rejected. These tests must pass branch CI; they are not real-game validation.
- Added an offline Sudoku backtracking solver with minimum-remaining-values cell selection, input validation, and solution counting capped at two to distinguish unique from ambiguous puzzles; unit tests cover normal, invalid, ambiguous, and non-mutating cases.
- Added an in-app offline Sudoku tool reachable from the Games screen. It accepts a manually entered 9×9 grid, reports formatting/consistency/solvability errors, and displays a result only when the puzzle has a unique solution. A dedicated parser rejects invalid characters instead of silently dropping them, with unit tests.
- Amaze GO remains observation-only: the recognizer can be injected into the adapter and successful observations can produce a validated path plan, but there is no calibrated default configuration, no planner-to-execution connection, and no victory/failure detection. It does not yet make the game playable autonomously.
- Sudoku, EA SPORTS FC Mobile and the other catalogue entries are not autonomous players. Listing a game does not mean it is supported.
- Learning screen controls are not connected to a real gameplay learning pipeline; scores must not be interpreted as measured capability.
- Cloud AI provider routing is not configured. The app does not currently offer automatic selection among external AI services.
- No physical-device, UI, permission-revocation or real-game tests have been performed.

## Validation

- Successful unit-test-only CI run before the latest grid-observation changes: https://github.com/pippo2801/crea-bot/actions/runs/38144908165
- The grid-observation validator and planner integration were committed afterward; the latest main-branch CI run should be checked separately.
- The recognizer branch's earlier CI run passed. New commits adding validated planning and its integration tests require their own successful CI run before being considered validated.
- CI does not compile or publish an APK. A successful unit-test run is not proof of runtime correctness.

## Release gates

1. Pass CI for the latest commit.
2. Calibrate the recognizer against real Amaze GO screenshots and verify grid representation and path plans before any execution integration.
3. Add integration tests for screen capture, gesture dispatch, permission denial/revocation and stop behavior.
4. After explicit authorization, build an APK and test on the user's phone.
5. Certify a game only after repeatable success/failure tests on real game screens.
