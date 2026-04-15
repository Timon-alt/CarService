package com.example.carservice.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int = 0,
    @SerialName("car_id")
    val carId: Int,
    @SerialName("service_id")
    val serviceId: Int,
    @SerialName("order_number")
    val orderNumber: String? = null,
    val status: String = "в ожидании",
    val description: String? = null,
    @SerialName("total_cost")
    val totalCost: Double = 0.0,
    @SerialName("appointment_date")
    val appointmentDate: String,  // дата записи
    @SerialName("appointment_time")
    val appointmentTime: String,  // время записи
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("completed_at")
    val completedAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

// DTO для вставки в базу данных (имена полей как в Supabase)
@Serializable
data class OrderInsertDto(
    @SerialName("car_id")
    val carId: Int,
    @SerialName("service_id")
    val serviceId: Int,
    @SerialName("order_number")
    val orderNumber: String,
    @SerialName("total_cost")
    val totalCost: Double,
    @SerialName("appointment_date")
    val appointmentDate: String,
    @SerialName("appointment_time")
    val appointmentTime: String,
    val description: String,
    val status: String = "в ожидании"
)

// Для создания заказа в доменном слое
@Serializable
data class CreateOrderRequest(
    val carId: Int,
    val serviceId: Int,
    val totalCost: Double,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime,
    val description: String? = null
)
