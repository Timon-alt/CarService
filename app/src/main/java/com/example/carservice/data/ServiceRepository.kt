package com.example.carservice.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

interface ServiceRepository {
    suspend fun getAllServices(): List<Service>
    suspend fun getServiceByCategory(category: String): List<Service>
    suspend fun getServiceById(id: Int): Service?
}

class ServiceRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : ServiceRepository {

    override suspend fun getAllServices(): List<Service> {
        return try {
            supabaseClient.postgrest
                .from("services")
                .select()
                .decodeList<Service>()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getServiceByCategory(category: String): List<Service> {
        return try {
            supabaseClient.postgrest
                .from("services")
                .select {
                    filter {
                        eq("category", category)
                    }
                }
                .decodeList<Service>()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getServiceById(id: Int): Service? {
        return try {
            supabaseClient.postgrest
                .from("services")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeList<Service>()
                .firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}