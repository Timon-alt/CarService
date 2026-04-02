package com.example.carservice.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.model.CreateCustomerRequest
import com.example.carservice.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authStateFlow().collect { isAuthenticated ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = isAuthenticated,
                    isLoading = false
                )
            }
        }

        _uiState.value = _uiState.value.copy(
            isAuthenticated = authRepository.isAuthenticated
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun updateFirstName(firstName: String) {
        _uiState.value = _uiState.value.copy(firstName = firstName)
    }

    fun updateLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun updateIsLoginMode(isLoginMode: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoginMode = isLoginMode,
            error = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun signIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authRepository.signIn(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            result.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Ошибка входа"
                )
            }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            // Валидация
            if (_uiState.value.password != _uiState.value.confirmPassword) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Пароли не совпадают"
                )
                return@launch
            }

            if (_uiState.value.firstName.isBlank() || _uiState.value.lastName.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Пожалуйста, введите имя и фамилию"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Сначала регистрируем пользователя
            val signUpResult = authRepository.signUp(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            signUpResult.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Ошибка регистрации"
                )
                return@launch
            }

            // Получаем ID только что созданного пользователя
            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка получения данных пользователя"
                )
                return@launch
            }

            // Создаем запись в таблице customers
            val customerResult = customerRepository.createCustomer(
                CreateCustomerRequest(
                    userId = userId,
                    firstName = _uiState.value.firstName,
                    lastName = _uiState.value.lastName,
                    phone = _uiState.value.phone.takeIf { it.isNotBlank() },
                    email = _uiState.value.email
                )
            )

            customerResult.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка сохранения данных: ${exception.message}"
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginMode = true,
                    error = "Регистрация успешна! Теперь войдите."
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)