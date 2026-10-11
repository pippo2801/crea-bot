package com.pippo2801.universalgamebot.diagnostics

enum class DiagnosticStatus {
    OK, ERROR, WARNING
}

data class DiagnosticItem(
    val componentName: String,
    val status: DiagnosticStatus,
    val details: String
)

/**
 * Reports only capabilities that are actually implemented or explicitly observed.
 * A registered game catalogue is not equivalent to a working detector or solver.
 */
class DiagnosticsManager {

    fun runDiagnostics(
        hasAccessibility: Boolean,
        hasScreenCapture: Boolean,
        isObservationRunning: Boolean = false
    ): List<DiagnosticItem> = listOf(
        DiagnosticItem(
            "Android System",
            DiagnosticStatus.OK,
            "App runtime available; device-specific behavior still needs phone testing"
        ),
        DiagnosticItem(
            "Screen Capture",
            if (hasScreenCapture) DiagnosticStatus.OK else DiagnosticStatus.WARNING,
            if (hasScreenCapture) "MediaProjection initialized" else "Grant screen-capture permission by starting observation"
        ),
        DiagnosticItem(
            "Accessibility Service",
            if (hasAccessibility) DiagnosticStatus.OK else DiagnosticStatus.WARNING,
            if (hasAccessibility) "Service enabled in Android settings" else "Enable only if you want to use future gesture controls"
        ),
        DiagnosticItem(
            "Observation Loop",
            if (isObservationRunning) DiagnosticStatus.OK else DiagnosticStatus.WARNING,
            if (isObservationRunning) "Observation loop running" else "Not running"
        ),
        DiagnosticItem(
            "Vision Engine",
            DiagnosticStatus.WARNING,
            "Reads basic bitmap metadata only; no semantic game/object recognition"
        ),
        DiagnosticItem(
            "Game Detection",
            DiagnosticStatus.WARNING,
            "No validated visual fingerprints; games currently remain Unknown"
        ),
        DiagnosticItem(
            "Game Strategies",
            DiagnosticStatus.WARNING,
            "Game-specific solvers are incomplete; no autonomous gameplay is certified"
        ),
        DiagnosticItem(
            "Learning",
            DiagnosticStatus.WARNING,
            "Learning progress is not automatically inferred from real gameplay yet"
        ),
        DiagnosticItem(
            "Verification",
            DiagnosticStatus.WARNING,
            "Certificates are validated by code, but no game has completed a real-device test suite"
        ),
        DiagnosticItem(
            "AI Router",
            DiagnosticStatus.WARNING,
            "No cloud provider routing is configured; the current implementation is offline scaffolding"
        ),
        DiagnosticItem(
            "Safety Manager",
            DiagnosticStatus.OK,
            "Fail-closed checks block unknown screens, errors and low-confidence observations"
        )
    )
}
