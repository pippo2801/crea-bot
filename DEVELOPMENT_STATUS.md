# Universal Game Bot — Development Status

## Verified repository state

- Android native app with Jetpack Compose navigation and a game catalogue.
- GitHub Actions configures Java 17, Android SDK 34 and Gradle 8.6.
- CI runs unit tests only. It does not invoke `assembleDebug`, create or publish an APK.
- The last confirmed CI run for commit `670e3c5696e18be948d6ee0b20092511e20b94ba` passed: [workflow run](https://github.com/pippo2801/crea-bot/actions/runs/38140236869). Later source changes must pass their own run before being considered validated.
- The Home screen now requests Android screen-capture consent before starting observation, and no longer displays a hard-coded GREEN safety state.
- Settings provides a shortcut to Android Accessibility settings.
- Screen capture now registers a MediaProjection callback, validates display dimensions, releases old resources before restart, and handles padded image rows.

## Safety and honesty

- Game profiles begin with learning score 0 and unverified.
- Learning score alone cannot set `AUTONOMY_VERIFIED`; high scores remain unverified until a matching successful certificate is applied.
- The game detector returns `Unknown Game` because validated visual fingerprints are not implemented.
- The vision engine currently reports image metadata only and deliberately reports recognition confidence 0.0.
- Safety starts in RED. Unknown states, errors and invalid or low confidence block actions.
- The action engine requires explicit authorization from the shared SafetyManager; invalid coordinates and unreasonable durations are rejected.
- Amaze GO remains observation-only until grid recognition, path planning and victory/failure detection are implemented.
- Sudoku and EA SPORTS FC Mobile adapters are scaffolds, not autonomous players.

## Not yet ready for release

- Latest source changes still need a successful CI run.
- No APK has been created or installed, following the user's explicit instruction not to create or compile one without permission.
- No real-device or UI permission-flow test has been performed.
- No validated game-specific vision model, working game solver, victory/failure recognition or autonomous gameplay has been demonstrated.
- No game is certified as autonomous.

## Next steps

1. Confirm CI passes for the latest commit.
2. Add focused tests for screen-capture lifecycle and permission-denial handling where testable.
3. Only after explicit user authorization, package an APK and perform staged phone tests.
4. Implement and validate each game adapter independently. Never infer support for all registered games from the catalogue.
