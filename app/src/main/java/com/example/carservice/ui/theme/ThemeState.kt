package com.example.carservice.ui.theme

import com.example.carservice.domain.model.ThemeMode

data class ThemeState(
    val currentTheme: ThemeMode = ThemeMode.System,
    val isInitialized: Boolean = false
)