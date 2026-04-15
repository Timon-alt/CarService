package com.example.carservice.domain.repository

import com.example.carservice.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun saveThemeMode(mode: ThemeMode)
    fun getThemeMode(): Flow<ThemeMode>
}