package com.example.carservice.data.repository

import com.example.carservice.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override val currentUserId: String?
        get() = supabaseClient.auth.currentUserOrNull()?.id

    override val isAuthenticated: Boolean
        get() = supabaseClient.auth.currentUserOrNull() != null

    override val currentUserEmail: String?
        get() = supabaseClient.auth.currentUserOrNull()?.email

    override fun authStateFlow(): Flow<Boolean> {
        // Правильный способ из документации Supabase
        return supabaseClient.auth.sessionStatus.map { sessionStatus ->
            when (sessionStatus) {
                is SessionStatus.Authenticated -> true
                is SessionStatus.NotAuthenticated -> false
                is SessionStatus.Initializing -> false
                is SessionStatus.RefreshFailure -> false
            }
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        supabaseClient.auth.signOut()
    }
}