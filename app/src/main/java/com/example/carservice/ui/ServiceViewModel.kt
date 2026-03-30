package com.example.carservice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.data.Service
import com.example.carservice.data.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServiceUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val category: String = "",
    val error: String? = null
)

class ServiceViewModel(
    private val category: String,
    private val serviceRepository: ServiceRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState(category = category))
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val services = serviceRepository.getServiceByCategory(category)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    services = services
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки услуг"
                )
            }
        }
    }
}