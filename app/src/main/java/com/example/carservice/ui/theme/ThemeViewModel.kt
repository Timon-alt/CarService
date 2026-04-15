package com.example.carservice.ui.theme

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.domain.model.ThemeMode
import com.example.carservice.domain.usecase.GetThemeModeUseCase
import com.example.carservice.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ThemeState())
    val state: StateFlow<ThemeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                getThemeModeUseCase().collect { themeMode ->
                    Log.d("ThemeViewModel", "Received theme mode: $themeMode")
                    _state.update { it.copy(currentTheme = themeMode, isInitialized = true) }
                }
            } catch (e: Exception) {
                Log.e("ThemeViewModel", "Error loading theme", e)
                _state.update { it.copy(isInitialized = true) }
            }
        }
    }

    private fun observeThemeChanges() {
        getThemeModeUseCase()
            .onEach { themeMode ->
                _state.update { it.copy(currentTheme = themeMode, isInitialized = true) }
                applyTheme(themeMode)
            }
            .launchIn(viewModelScope)
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(mode)
        }
    }

    private fun applyTheme(themeMode: ThemeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode.modeValue)
    }
}