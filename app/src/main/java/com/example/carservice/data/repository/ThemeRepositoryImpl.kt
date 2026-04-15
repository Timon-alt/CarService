package com.example.carservice.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.carservice.domain.model.ThemeMode
import com.example.carservice.domain.repository.ThemeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ThemeRepositoryImpl(
    private val context: Context
) : ThemeRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val THEME_KEY = "theme_mode"

    override suspend fun saveThemeMode(mode: ThemeMode) {
        prefs.edit().putInt(THEME_KEY, mode.modeValue).apply()
    }

    override fun getThemeMode(): Flow<ThemeMode> = callbackFlow {
        val initialValue = prefs.getInt(THEME_KEY, ThemeMode.System.modeValue)
        trySend(ThemeMode.fromValue(initialValue))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == THEME_KEY) {
                val newValue = prefs.getInt(THEME_KEY, ThemeMode.System.modeValue)
                trySend(ThemeMode.fromValue(newValue))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()
}
