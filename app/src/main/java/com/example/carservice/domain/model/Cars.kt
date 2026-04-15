package com.example.carservice.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cars(
    val id: Int,
    @SerialName("customer_id")
    val customerId: String,
    val brand: String,
    val model: String,
    val vin: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class CreateCarRequest(
    @SerialName("customer_id")
    val customerId: String,

    @SerialName("brand")
    val brand: String,

    @SerialName("model")
    val model: String,

    @SerialName("vin")
    val vin: String
)

@Serializable
data class UpdateCarRequest(
    @SerialName("brand")
    val brand: String? = null,

    @SerialName("model")
    val model: String? = null,

    @SerialName("vin")
    val vin: String? = null
)