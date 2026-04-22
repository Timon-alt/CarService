package com.example.carservice.di

import com.example.carservice.data.repository.AuthRepositoryImpl
import com.example.carservice.data.repository.CarsRepositoryImpl
import com.example.carservice.data.repository.CustomerRepositoryImpl
import com.example.carservice.data.repository.OrderRepositoryImpl
import com.example.carservice.data.repository.ServiceRepositoryImpl
import com.example.carservice.data.repository.ThemeRepositoryImpl
import com.example.carservice.domain.repository.AuthRepository
import com.example.carservice.domain.repository.CarsRepository
import com.example.carservice.domain.repository.CustomerRepository
import com.example.carservice.domain.repository.OrderRepository
import com.example.carservice.domain.repository.ServiceRepository
import com.example.carservice.domain.repository.ThemeRepository
import com.example.carservice.domain.usecase.GetThemeModeUseCase
import com.example.carservice.domain.usecase.SetThemeModeUseCase
import com.example.carservice.ui.features.auth.AuthViewModel
import com.example.carservice.ui.features.history.HistoryViewModel
import com.example.carservice.ui.features.home.CarMaintenanceViewModel
import com.example.carservice.ui.features.profile.ProfileViewModel
import com.example.carservice.ui.features.home.ServiceViewModel
import com.example.carservice.ui.features.profile.GarageViewModel
import com.example.carservice.ui.theme.ThemeViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Supabase
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

    single<ThemeRepository> {
        ThemeRepositoryImpl(
            context = androidContext()
        )
    }

    single<CarsRepository> {
        CarsRepositoryImpl(
            supabaseClient = get()
        )
    }

    single<OrderRepository> {
        OrderRepositoryImpl(
            supabaseClient = get()
        )
    }

    // UseCases
    factory { GetThemeModeUseCase(repository = get()) }
    factory { SetThemeModeUseCase(repository = get()) }

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

    viewModel {
        ThemeViewModel(
            getThemeModeUseCase = get(),
            setThemeModeUseCase = get()
        )
    }

    viewModel {
        GarageViewModel(
            authRepository = get(),
            carsRepository = get(),
            customerRepository = get()
        )
    }

    viewModel {
        CarMaintenanceViewModel(
            authRepository = get(),
            carsRepository = get(),
            customerRepository = get(),
            serviceRepository = get(),
            orderRepository = get()
        )
    }

    viewModel {
        HistoryViewModel(
            authRepository = get(),
            orderRepository = get(),
            carsRepository = get(),
            customerRepository = get(),
            serviceRepository = get()
        )
    }
}