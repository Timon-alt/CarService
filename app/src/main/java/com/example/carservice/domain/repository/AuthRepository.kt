package com.example.carservice.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isAuthenticated: Boolean
    val currentUserEmail: String?

    fun authStateFlow(): Flow<Boolean>  // Этот метод должен возвращать поток изменений
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}