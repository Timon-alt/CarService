package com.example.carservice.data.repository

import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.CreateOrderRequest
import com.example.carservice.domain.model.Order
import com.example.carservice.domain.model.OrderInsertDto
import com.example.carservice.domain.repository.OrderRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

class OrderRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : OrderRepository {

    override suspend fun createOrder(request: CreateOrderRequest): Result<Order> {
        return try {
            val orderNumber = generateOrderNumber()

            // Форматируем дату и время в ISO формат (YYYY-MM-DD и HH:MM:SS)
            // request.appointmentDate и appointmentTime уже kotlinx.datetime
            val dateStr = request.appointmentDate.toString() // даст "YYYY-MM-DD"
            val timeStr = request.appointmentTime.toString().take(8) // даст "HH:MM:SS" или "HH:MM"
            
            // Убедимся, что время в формате HH:MM:SS
            val finalTimeStr = if (timeStr.length == 5) "$timeStr:00" else timeStr

            val orderDto = OrderInsertDto(
                carId = request.carId,
                serviceId = request.serviceId,
                orderNumber = orderNumber,
                totalCost = request.totalCost,
                appointmentDate = dateStr,
                appointmentTime = finalTimeStr,
                description = request.description ?: "",
                status = "в ожидании"
            )

            // Вставляем данные используя DTO (это решает проблему с Serializer for class 'Any')
            supabaseClient.postgrest
                .from("orders")
                .insert(orderDto)

            // Получаем созданный заказ отдельным запросом
            val createdOrder = supabaseClient.postgrest
                .from("orders")
                .select {
                    filter {
                        eq("order_number", orderNumber)
                    }
                }
                .decodeList<Order>()
                .firstOrNull()

            if (createdOrder != null) {
                Result.success(createdOrder)
            } else {
                Result.failure(Exception("Order not found after creation"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByCarId(carId: Int): Result<List<Order>> {
        return try {
            val orders = supabaseClient.postgrest
                .from("orders")
                .select {
                    filter {
                        eq("car_id", carId)
                    }
                }
                .decodeList<Order>()

            Result.success(orders)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getOrdersByCustomerId(customerId: String): Result<List<Order>> {
        return try {
            val cars = supabaseClient.postgrest
                .from("cars")
                .select {
                    filter {
                        eq("customer_id", customerId)
                    }
                }
                .decodeList<Cars>()

            if (cars.isEmpty()) {
                return Result.success(emptyList())
            }

            val carIds = cars.map { it.id }

            val orders = supabaseClient.postgrest
                .from("orders")
                .select {
                    filter {
                        filter("car_id", FilterOperator.IN, carIds)
                    }
                }
                .decodeList<Order>()

            Result.success(orders)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("orders")
                .update(
                    mapOf("status" to status) // Здесь Map<String, String> работает, так как тип String известен
                ) {
                    filter {
                        eq("id", orderId)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getOrderById(orderId: Int): Result<Order?> {
        return try {
            val order = supabaseClient.postgrest
                .from("orders")
                .select {
                    filter {
                        eq("id", orderId)
                    }
                }
                .decodeList<Order>()
                .firstOrNull()

            Result.success(order)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "ORD-${timestamp}-$random"
    }
}
