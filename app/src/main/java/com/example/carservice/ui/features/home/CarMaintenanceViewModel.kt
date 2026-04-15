package com.example.carservice.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.CreateOrderRequest
import com.example.carservice.domain.model.Order
import com.example.carservice.domain.model.Service
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.repository.CarsRepository
import com.example.carservice.domain.repository.CustomerRepository
import com.example.carservice.domain.repository.OrderRepository
import com.example.carservice.domain.repository.ServiceRepository
import com.example.carservice.utils.DateConverters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime
import kotlinx.datetime.LocalDate as KotlinLocalDate
import kotlinx.datetime.LocalTime as KotlinLocalTime

class CarMaintenanceViewModel(
    private val authRepository: AuthRepository,
    private val customerRepository: CustomerRepository,
    private val carsRepository: CarsRepository,
    private val serviceRepository: ServiceRepository,
    private val orderRepository: OrderRepository  // ← Добавляем репозиторий заказов
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarMaintenanceUiState(isLoading = true))
    val uiState: StateFlow<CarMaintenanceUiState> = _uiState.asStateFlow()

    // ← Добавляем состояние для бронирования
    private val _bookingState = MutableStateFlow(BookingState())
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private var customerId: String? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Загружаем customerId
            val userId = authRepository.currentUserId
            if (userId != null) {
                val customerResult = customerRepository.getCustomer(userId)
                customerResult.onSuccess { customer ->
                    customerId = customer?.id
                }
            }

            // Загружаем список автомобилей пользователя
            if (customerId != null) {
                val carsResult = carsRepository.getCarsByCustomerId(customerId!!)
                carsResult.onSuccess { cars ->
                    _uiState.value = _uiState.value.copy(
                        cars = cars,
                        selectedCar = cars.firstOrNull()
                    )
                }
            }

            // Загружаем список услуг через ваш репозиторий
            try {
                val allServices = serviceRepository.getAllServices()
                val categories = allServices
                    .mapNotNull { it.category }
                    .distinct()

                _uiState.value = _uiState.value.copy(
                    services = allServices,
                    categories = categories,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Ошибка загрузки услуг: ${e.message}"
                )
            }
        }
    }

    fun selectCar(car: Cars) {
        _uiState.value = _uiState.value.copy(selectedCar = car)
    }

    fun selectService(service: Service) {
        _uiState.value = _uiState.value.copy(selectedService = service)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterServices()
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        filterServices()
    }

    private fun filterServices() {
        val services = _uiState.value.services
        val query = _uiState.value.searchQuery.lowercase().trim()
        val category = _uiState.value.selectedCategory

        val filtered = services.filter { service ->
            val matchesQuery = query.isEmpty() ||
                    service.name.lowercase().contains(query) ||
                    service.description?.lowercase()?.contains(query) == true

            val matchesCategory = category == null || service.category == category

            matchesQuery && matchesCategory
        }

        _uiState.value = _uiState.value.copy(filteredServices = filtered)
    }

    fun getTotalPrice(): Double {
        return _uiState.value.selectedService?.price ?: 0.0  // ← изменил с Double.MIN_VALUE на 0.0
    }

    fun isNextButtonEnabled(): Boolean {
        return _uiState.value.selectedCar != null &&
                _uiState.value.selectedService != null
    }

    // ← Метод принимает java.time (из UI), конвертирует в kotlinx.datetime
    fun bookAppointment(
        appointmentDate: JavaLocalDate,  // ← из календаря (java.time)
        appointmentTime: JavaLocalTime,  // ← из UI (java.time)
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val selectedCar = _uiState.value.selectedCar
            val selectedService = _uiState.value.selectedService

            if (selectedCar == null || selectedService == null) {
                val error = "Выберите автомобиль и услугу"
                _bookingState.value = _bookingState.value.copy(error = error)
                onError(error)
                return@launch
            }

            _bookingState.value = _bookingState.value.copy(isLoading = true, error = null)

            // ← КОНВЕРТАЦИЯ: java.time → kotlinx.datetime
            val kotlinxDate = DateConverters.toKotlinx(appointmentDate)
            val kotlinxTime = DateConverters.toKotlinx(appointmentTime)

            val request = CreateOrderRequest(
                carId = selectedCar.id,
                serviceId = selectedService.id,
                totalCost = selectedService.price,
                appointmentDate = kotlinxDate,  // ← kotlinx.datetime
                appointmentTime = kotlinxTime,  // ← kotlinx.datetime
                description = "${selectedService.name} для ${selectedCar.brand} ${selectedCar.model}"
            )

            val result = orderRepository.createOrder(request)
            result.onSuccess { order ->
                _bookingState.value = _bookingState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    createdOrder = order,
                    error = null
                )
                onSuccess()
            }.onFailure { exception ->
                _bookingState.value = _bookingState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Ошибка создания заказа"
                )
                onError(exception.message ?: "Ошибка создания заказа")
            }
        }
    }

    // ← НОВАЯ ФУНКЦИЯ: Сброс состояния бронирования
    fun resetBookingState() {
        _bookingState.value = BookingState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

// ← НОВЫЙ DATA CLASS: Состояние бронирования
data class BookingState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val createdOrder: Order? = null
)

data class CarMaintenanceUiState(
    val isLoading: Boolean = false,
    val selectedCar: Cars? = null,
    val selectedService: Service? = null,
    val cars: List<Cars> = emptyList(),
    val services: List<Service> = emptyList(),
    val filteredServices: List<Service> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val error: String? = null
)