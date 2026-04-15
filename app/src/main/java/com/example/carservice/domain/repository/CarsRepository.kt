package com.example.carservice.domain.repository

import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.CreateCarRequest
import com.example.carservice.domain.model.UpdateCarRequest
import kotlinx.coroutines.flow.Flow

interface CarsRepository {
    suspend fun getCarsByCustomerId(customerId: String): Result<List<Cars>>
    suspend fun getCar(carId: String): Result<Cars?>
    suspend fun createCar(request: CreateCarRequest): Result<Cars>
    suspend fun updateCar(carId: String, request: UpdateCarRequest): Result<Cars>
    suspend fun deleteCar(carId: Int): Result<Unit>
    fun getCarsFlow(customerId: String): Flow<List<Cars>>
}