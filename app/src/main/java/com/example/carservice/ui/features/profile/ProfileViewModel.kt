package com.example.carservice.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.model.Customer
import com.example.carservice.domain.repository.CustomerRepository
import com.example.carservice.domain.model.UpdateCustomerRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Пользователь не авторизован"
                )
                return@launch
            }

            val result = customerRepository.getCustomer(userId)
            result.onSuccess { customer ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    customer = customer,
                    email = authRepository.currentUserEmail ?: "",
                    isEditMode = false
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки данных: ${exception.message}"
                )
            }
        }
    }

    fun updateFirstName(firstName: String) {
        _uiState.value = _uiState.value.copy(editFirstName = firstName)
    }

    fun updateLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(editLastName = lastName)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(editPhone = phone)
    }

    fun toggleEditMode() {
        val customer = _uiState.value.customer
        if (customer != null && !_uiState.value.isEditMode) {
            _uiState.value = _uiState.value.copy(
                isEditMode = true,
                editFirstName = customer.firstName,
                editLastName = customer.lastName,
                editPhone = customer.phone ?: ""
            )
        } else {
            _uiState.value = _uiState.value.copy(isEditMode = false)
        }
    }

    fun saveChanges() {
        val currentState = _uiState.value
        val customer = currentState.customer ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Формируем запрос на обновление.
            // Если поле пустое или совпадает с текущим, передаем null (Supabase пропустит это поле при PATCH запросе)
            val updateRequest = UpdateCustomerRequest(
                firstName = if (currentState.editFirstName.isNotBlank() && currentState.editFirstName != customer.firstName) {
                    currentState.editFirstName
                } else null,
                
                lastName = if (currentState.editLastName.isNotBlank() && currentState.editLastName != customer.lastName) {
                    currentState.editLastName
                } else null,
                
                phone = if (currentState.editPhone != (customer.phone ?: "")) {
                    currentState.editPhone.ifBlank { null } // Позволяем удалить телефон (сделать null), если стерли
                } else null
            )

            // Если ничего не изменилось, просто выходим из режима редактирования
            if (updateRequest.firstName == null && updateRequest.lastName == null && updateRequest.phone == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, isEditMode = false)
                return@launch
            }

            val userId = authRepository.currentUserId ?: return@launch
            val result = customerRepository.updateCustomer(userId, updateRequest)

            result.onSuccess { updatedCustomer ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    customer = updatedCustomer,
                    isEditMode = false,
                    error = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка сохранения: ${exception.message}"
                )
            }
        }
    }

    fun signOut(onSignOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onSignOut()
        }
    }

    data class ProfileUiState(
        val isLoading: Boolean = false,
        val customer: Customer? = null,
        val email: String = "",
        val isEditMode: Boolean = false,
        val editFirstName: String = "",
        val editLastName: String = "",
        val editPhone: String = "",
        val error: String? = null
    )
}
