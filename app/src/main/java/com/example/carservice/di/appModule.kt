package com.example.carservice.di

import com.example.carservice.data.repository.AuthRepositoryImpl
import com.example.carservice.data.repository.CustomerRepositoryImpl
import com.example.carservice.data.repository.ServiceRepositoryImpl
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.repository.CustomerRepository
import com.example.carservice.domain.repository.ServiceRepository
import com.example.carservice.ui.features.auth.AuthViewModel
import com.example.carservice.ui.features.profile.ProfileViewModel
import com.example.carservice.ui.features.home.ServiceViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://tkenahiikmhnfahyejyz.supabase.co",
            supabaseKey = "sb_publishable_b_jySrM8DKMP0lwAZ2KURA_yu6jodaf"
        ) {
            install(Postgrest)
            install(Auth)
        }
    }

    // Репозитории
    single<CustomerRepository> {
        CustomerRepositoryImpl(
            supabaseClient = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            supabaseClient = get()
        )
    }

    single<ServiceRepository> {
        ServiceRepositoryImpl(
            supabaseClient = get()
        )
    }

    // UseCases

    //ViewModels
    viewModel { (category: String) ->
        ServiceViewModel(
            category = category,
            serviceRepository = get()
        )
    }

    viewModel {
        AuthViewModel(
        authRepository = get(),
            customerRepository = get()
        )
    }

    viewModel {
        ProfileViewModel(
            authRepository = get(),
            customerRepository = get()
        )
    }
}