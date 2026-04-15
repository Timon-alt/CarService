package com.example.carservice.domain.usecase

import com.example.carservice.domain.model.ThemeMode
import com.example.carservice.domain.repository.ThemeRepository

class SetThemeModeUseCase(
    private val repository: ThemeRepository
) {
    suspend operator fun invoke(mode: ThemeMode) {
        repository.saveThemeMode(mode)
    }
}