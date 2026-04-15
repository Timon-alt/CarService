package com.example.carservice.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String? = null
)