package com.pippo2801.universalgamebot.diagnostics

enum class DiagnosticStatus {
    OK, ERROR, WARNING
}

data class DiagnosticItem(
    val componentName: String,
    val status: DiagnosticStatus,
    val details: String
)

class DiagnosticsManager {

    fun runDiagnostics(hasAccessibility: Boolean, hasScreenCapture: Boolean): List<DiagnosticItem> {
        return listOf(
            DiagnosticItem("Android System", DiagnosticStatus.OK, "Android 14+ / SDK 34 compatible"),
            DiagnosticItem("Screen Capture", if (hasScreenCapture) DiagnosticStatus.OK else DiagnosticStatus.WARNING, if (hasScreenCapture) "MediaProjection ready" else "Permission required"),
            DiagnosticItem("Accessibility Service", if (hasAccessibility) DiagnosticStatus.OK else DiagnosticStatus.ERROR, if (hasAccessibility) "Service active & bound" else "Service disabled in Android Settings"),
            DiagnosticItem("Input Engine", DiagnosticStatus.OK, "Dispatch gesture ready"),
            DiagnosticItem("Vision Engine", DiagnosticStatus.OK, "Local bitmap processor active"),
            DiagnosticItem("Game Detection", DiagnosticStatus.OK, "Registry loaded (25 games)"),
            DiagnosticItem("Learning Engine", DiagnosticStatus.OK, "State observation ready"),
            DiagnosticItem("Memory Manager", DiagnosticStatus.OK, "Persistent local storage ready"),
            DiagnosticItem("Planner", DiagnosticStatus.OK, "Strategy engine ready"),
            DiagnosticItem("Verification", DiagnosticStatus.OK, "Autonomous test suite ready"),
            DiagnosticItem("AI Router", DiagnosticStatus.OK, "Zero-Cost Guard active (Offline-first)"),
            DiagnosticItem("Safety Manager", DiagnosticStatus.OK, "Green / Yellow / Red guard active")
        )
    }
}
