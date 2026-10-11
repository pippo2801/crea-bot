# Universal Game Bot — Development Status

## Current verified source state

- Android native project exists with Compose navigation and game catalogue.
- GitHub Actions initializes Java 17, Android SDK 34 and Gradle 8.6.
- CI runs unit tests only. It does not invoke `assembleDebug` and does not publish an APK.
- `TEST_PLAN.md` documents staged CI, permission, observation, safety and per-game tests.
- Unit tests cover learning-score boundaries, autonomy certificate conditions, game-profile verification, unknown-game detection and fail-closed safety decisions.

## Safety and honesty fixes

- Game profiles begin at learning score 0 and unverified.
- Learning score alone can never set `AUTONOMY_VERIFIED`; at 90–100 the status is `NEARLY_READY` until a matching successful certificate is applied.
- The verification engine no longer invents passing tests or a 98% confidence score.
- A profile can be certified only by a matching certificate with a non-zero test count, all checks passing, zero failures, confidence at least 1.0 and matching game/profile version.
- The game detector returns `Unknown Game` because validated visual fingerprints are not implemented yet.
- The vision engine currently reports basic image metadata only and deliberately reports recognition confidence 0.0.
- Safety starts in RED. Unknown states, errors, invalid confidence and medium confidence block actions.
- The action engine requires the same explicitly injected safety manager to authorize non-NONE actions; invalid coordinates and unreasonable durations are rejected.
- Amaze GO's adapter is observation-only until actual grid recognition, safe path planning and victory/failure detection exist.
- Sudoku and EA SPORTS FC Mobile adapters are still scaffolds and are not autonomous.

## CI status

Latest relevant run: [GitHub Actions](https://github.com/pippo2801/crea-bot/actions).

The latest run observed during the current work had completed SDK and Gradle setup and was still executing `gradle --no-daemon testDebugUnitTest`. Its final result has not yet been confirmed. Do not describe the tests as passing until a completed run reports success.

## Not yet complete

- No successful final CI test run confirmed for the latest source.
- No APK created or installed, by explicit user instruction.
- No real-device tests performed.
- No validated game-specific vision models, grid solver, victory/failure recognition or autonomous gameplay demonstrated.
- No game is currently certified as autonomous.

## Next steps

1. Obtain a successful CI run for the latest commit; fix any compilation/test failures it reveals.
2. Expand automated tests as source components become testable.
3. Only after explicit user authorization, package an APK and begin staged phone testing.
4. Implement and validate each game adapter independently. Never infer support for all registered games from the catalogue.
