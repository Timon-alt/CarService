package com.example.carservice.domain.model

import androidx.appcompat.app.AppCompatDelegate

sealed class ThemeMode(val modeValue: Int) {
    data object Light : ThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
    data object Dark : ThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
    data object System : ThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    companion object {
        fun fromValue(value: Int): ThemeMode = when (value) {
            AppCompatDelegate.MODE_NIGHT_NO -> Light
            AppCompatDelegate.MODE_NIGHT_YES -> Dark
            else -> System
        }

        fun getDisplayName(mode: ThemeMode): String = when (mode) {
            Light -> "Светлая"
            Dark -> "Тёмная"
            System -> "Системная"
        }
    }
}