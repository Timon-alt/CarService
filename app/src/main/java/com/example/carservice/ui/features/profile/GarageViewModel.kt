package com.example.carservice.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.CreateCarRequest
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.repository.CarsRepository
import com.example.carservice.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GarageViewModel(
    private val authRepository: AuthRepository,
    private val customerRepository: CustomerRepository,
    private val carsRepository: CarsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GarageUiState(isLoading = true))
    val uiState: StateFlow<GarageUiState> = _uiState.asStateFlow()

    private var customerId: String? = null

    init {
        loadCustomerAndCars()
    }

    private fun loadCustomerAndCars() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Пользователь не авторизован"
                )
                return@launch
            }

            // Получаем customer_id
            val customerResult = customerRepository.getCustomer(userId)
            customerResult.onSuccess { customer ->
                if (customer != null) {
                    customerId = customer.id
                    loadCars()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Данные пользователя не найдены"
                    )
                }
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки данных: ${exception.message}"
                )
            }
        }
    }

    private fun loadCars() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val carsResult = carsRepository.getCarsByCustomerId(customerId!!)
            carsResult.onSuccess { cars ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cars = cars,
                    error = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки автомобилей: ${exception.message}"
                )
            }
        }
    }

    fun addCar(brand: String, model: String, vin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (customerId == null) {
                _uiState.value = _uiState.value.copy(
                    error = "Данные пользователя не найдены"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = CreateCarRequest(
                customerId = customerId!!,
                brand = brand,
                model = model,
                vin = vin.uppercase()
            )

            val result = carsRepository.createCar(request)
            result.onSuccess { newCar ->
                val currentCars = _uiState.value.cars.toMutableList()
                currentCars.add(0, newCar)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cars = currentCars,
                    error = null
                )
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка добавления: ${exception.message}"
                )
            }
        }
    }

    fun deleteCar(carId: Int) {
        viewModelScope.launch {
            val result = carsRepository.deleteCar(carId)
            result.onSuccess {
                val currentCars = _uiState.value.cars.filter { it.id != carId }
                _uiState.value = _uiState.value.copy(
                    cars = currentCars,
                    error = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = "Ошибка удаления: ${exception.message}"
                )
            }
        }
    }

    fun refreshCars() {
        if (customerId != null) {
            loadCars()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    data class GarageUiState(
        val isLoading: Boolean = false,
        val cars: List<Cars> = emptyList(),
        val error: String? = null
    )
}