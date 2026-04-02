package com.example.carservice.data.repository

import com.example.carservice.domain.model.CreateCustomerRequest
import com.example.carservice.domain.model.Customer
import com.example.carservice.domain.model.UpdateCustomerRequest
import com.example.carservice.domain.repository.CustomerRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CustomerRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : CustomerRepository {

    override suspend fun getCustomer(userId: String): Result<Customer?> = runCatching {
        supabaseClient.postgrest["customers"]
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeSingleOrNull<Customer>()
    }

    override suspend fun createCustomer(request: CreateCustomerRequest): Result<Customer> = runCatching {
        supabaseClient.postgrest["customers"]
            .insert(request)
            .decodeSingle<Customer>()
    }

    override suspend fun updateCustomer(userId: String, request: UpdateCustomerRequest): Result<Customer> = runCatching {
        supabaseClient.postgrest["customers"]
            .update(request) {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeSingle<Customer>()
    }

    override suspend fun deleteCustomer(userId: String): Result<Unit> = runCatching {
        supabaseClient.postgrest["customers"]
            .delete {
                filter {
                    eq("user_id", userId)
                }
            }
    }

    override fun getCustomerFlow(userId: String): Flow<Customer?> {
        // Используем обычный select для получения данных один раз
        // Если вам не нужны реальные обновления в реальном времени, это правильный подход
        return flow {
            val customer = supabaseClient.postgrest["customers"]
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull<Customer>()
            emit(customer)
        }
    }
}