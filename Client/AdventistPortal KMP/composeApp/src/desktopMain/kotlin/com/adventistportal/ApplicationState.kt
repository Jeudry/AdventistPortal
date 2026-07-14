package com.adventistportal

import androidx.compose.ui.window.TrayState
import com.adventistportal.windows.WindowState
import com.adventistportal.core.domain.preferences.ThemePreference

data class ApplicationState(
    val windows: List<WindowState> = listOf(WindowState()),
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val trayState: TrayState = TrayState()
)
