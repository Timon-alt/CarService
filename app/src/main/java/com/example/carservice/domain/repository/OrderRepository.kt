package com.example.carservice.domain.repository

import com.example.carservice.domain.model.CreateOrderRequest
import com.example.carservice.domain.model.Order

interface OrderRepository {
    suspend fun createOrder(request: CreateOrderRequest): Result<Order>
    suspend fun getOrdersByCarId(carId: Int): Result<List<Order>>
    suspend fun getOrdersByCustomerId(customerId: String): Result<List<Order>>
    suspend fun updateOrderStatus(orderId: Int, status: String): Result<Unit>
    suspend fun getOrderById(orderId: Int): Result<Order?>
}