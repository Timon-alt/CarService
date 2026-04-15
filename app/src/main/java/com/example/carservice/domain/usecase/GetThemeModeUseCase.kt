package com.example.carservice.domain.usecase

import com.example.carservice.domain.model.ThemeMode
import com.example.carservice.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class GetThemeModeUseCase(
    private val repository: ThemeRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repository.getThemeMode()
}