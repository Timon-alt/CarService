package com.example.carservice.data.repository

import android.R.attr.order
import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.CreateCarRequest
import com.example.carservice.domain.model.UpdateCarRequest
import com.example.carservice.domain.repository.CarsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CarsRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : CarsRepository {

    override suspend fun getCarsByCustomerId(customerId: String): Result<List<Cars>> = runCatching {
        supabaseClient.postgrest["cars"]
            .select {
                filter {
                    eq("customer_id", customerId)
                }
                order(column = "created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList<Cars>()
    }

    override suspend fun getCar(carId: String): Result<Cars?> = runCatching {
        supabaseClient.postgrest["cars"]
            .select {
                filter {
                    eq("id", carId)
                }
            }
            .decodeSingleOrNull<Cars>()
    }

    override suspend fun createCar(request: CreateCarRequest): Result<Cars> = runCatching {
        supabaseClient.postgrest["cars"]
            .insert(request)
            .decodeSingle<Cars>()
    }

    override suspend fun updateCar(carId: String, request: UpdateCarRequest): Result<Cars> = runCatching {
        supabaseClient.postgrest["cars"]
            .update(request) {
                filter {
                    eq("id", carId)
                }
            }
            .decodeSingle<Cars>()
    }

    override suspend fun deleteCar(carId: Int): Result<Unit> = runCatching {
        supabaseClient.postgrest["cars"]
            .delete {
                filter {
                    eq("id", carId)
                }
            }
    }

    override fun getCarsFlow(customerId: String): Flow<List<Cars>> {
        // Используем обычный select для получения данных один раз
        return flow {
            val cars = supabaseClient.postgrest["cars"]
                .select {
                    filter {
                        eq("customer_id", customerId)
                    }
                    order(column = "created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<Cars>()
            emit(cars)
        }
    }
}