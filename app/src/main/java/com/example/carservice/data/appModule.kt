package com.example.carservice.data

import android.R.attr.category
import com.example.carservice.ui.ServiceViewModel
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
        }
    }

    // Репозитории
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
}