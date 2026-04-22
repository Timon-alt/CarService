package com.example.carservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.carservice.di.appModule
import com.example.carservice.ui.CarServiceApp
import com.example.carservice.ui.theme.MainTheme
import com.example.carservice.ui.theme.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализируем Koin здесь, чтобы он был доступен до setContent
        // Проверяем, не запущен ли он уже (на случай пересоздания Activity)
        try {
            startKoin {
                androidContext(this@MainActivity)
                modules(appModule)
            }
        } catch (e: Exception) {
            // Koin уже запущен
        }

        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeState by themeViewModel.state.collectAsState()

            // Передаем выбранный режим темы в нашу MainTheme
            MainTheme(themeMode = themeState.currentTheme) {
                CarServiceApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Опционально: останавливаем Koin при уничтожении
        // stopKoin()
    }
}
