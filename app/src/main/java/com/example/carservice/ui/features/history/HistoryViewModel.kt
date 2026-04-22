package com.example.carservice.ui.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.Order
import com.example.carservice.domain.model.Service
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.repository.CarsRepository
import com.example.carservice.domain.repository.CustomerRepository
import com.example.carservice.domain.repository.OrderRepository
import com.example.carservice.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderDisplayItem(
    val order: Order,
    val car: Cars?,
    val service: Service?
)

data class HistoryUiState(
    val isLoading: Boolean = false,
    val orders: List<OrderDisplayItem> = emptyList(),
    val error: String? = null
)

class HistoryViewModel(
    private val authRepository: AuthRepository,
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val carsRepository: CarsRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val userId = authRepository.currentUserId ?: throw Exception("Пользователь не авторизован")
                
                // 1. Получаем customerId
                val customer = customerRepository.getCustomer(userId).getOrThrow() 
                    ?: throw Exception("Профиль пользователя не найден")
                
                // 2. Получаем все заказы
                val orders = orderRepository.getOrdersByCustomerId(customer.id).getOrThrow()
                
                // 3. Загружаем все машины и услуги пользователя для маппинга
                val cars = carsRepository.getCarsByCustomerId(customer.id).getOrThrow()
                val services = serviceRepository.getAllServices()
                
                // 4. Формируем список для отображения
                val displayItems = orders.map { order ->
                    OrderDisplayItem(
                        order = order,
                        car = cars.find { it.id == order.carId },
                        service = services.find { it.id == order.serviceId }
                    )
                }.sortedByDescending { it.order.appointmentDate } // Сначала свежие

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    orders = displayItems
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }
}
