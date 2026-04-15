package com.example.carservice.domain.repository

import com.example.carservice.domain.model.Service
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface ServiceRepository {
    suspend fun getAllServices(): List<Service>
    suspend fun getServiceByCategory(category: String): List<Service>
    suspend fun getServiceById(id: Int): Service?
    suspend fun getServiceByName(name: String): Service?
}