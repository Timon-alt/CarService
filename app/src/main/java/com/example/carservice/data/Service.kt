package com.example.carservice.data

import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: Int? = null,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val image: String? = null
)