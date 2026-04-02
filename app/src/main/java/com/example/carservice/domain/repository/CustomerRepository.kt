package com.example.carservice.domain.repository

import com.example.carservice.domain.model.CreateCustomerRequest
import com.example.carservice.domain.model.Customer
import com.example.carservice.domain.model.UpdateCustomerRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CustomerRepository {
    suspend fun getCustomer(userId: String): Result<Customer?>
    suspend fun createCustomer(request: CreateCustomerRequest): Result<Customer>
    suspend fun updateCustomer(userId: String, request: UpdateCustomerRequest): Result<Customer>
    suspend fun deleteCustomer(userId: String): Result<Unit>
    fun getCustomerFlow(userId: String): Flow<Customer?>
}