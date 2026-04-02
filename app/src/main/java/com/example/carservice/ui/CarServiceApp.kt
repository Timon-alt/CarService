package com.example.carservice.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carservice.di.appModule
import com.example.carservice.ui.features.auth.AuthScreen
import com.example.carservice.ui.features.auth.AuthViewModel
import com.example.carservice.ui.features.home.HomeScreen
import com.example.carservice.ui.features.profile.ProfileScreen
import com.example.carservice.ui.theme.MainTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun CarServiceApp(modifier: Modifier = Modifier) {
    KoinApplication(configuration = koinConfiguration {
        modules(appModule)
    }) {
        val authViewModel: AuthViewModel = koinViewModel()
        val uiState by authViewModel.uiState.collectAsState()

        // Показываем экран загрузки, пока идет инициализация
        if (uiState.isLoading) {
            // Экран загрузки
            LoadingScreen(modifier = modifier)
        } else if (!uiState.isAuthenticated) {
            // Показываем экран авторизации
            AuthScreen(
                onAuthenticated = {
                    // После успешной авторизации просто обновляем состояние
                },
                modifier = modifier
            )
        } else {
            // Показываем основное приложение
            MainAppContent(modifier = modifier)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MainTheme.colors.mainColor
        )
    }
}

@Composable
fun MainAppContent(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME

    var isBottomBarVisible by remember { mutableStateOf(true) }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val selectedDestination = Destination.entries.find { it.route == currentRoute }?.ordinal
        ?: startDestination.ordinal

    Scaffold(
        modifier = modifier,
        containerColor = MainTheme.colors.singleTheme,
        bottomBar = {
            if (isBottomBarVisible && currentRoute in listOf(
                    Destination.HOME.route,
                    Destination.HISTORY.route,
                    Destination.PROFILE.route
                )) {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    contentColor = MainTheme.colors.mainColor,
                    containerColor = MainTheme.colors.navigationBar
                ) {
                    Destination.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestination == index,
                            onClick = {
                                if (currentRoute != destination.route) {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController,
            startDestination,
            onBottomBarVisibilityChange = { isVisible ->
                isBottomBarVisible = isVisible
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("home", "Главная", Icons.Default.Home, "home"),
    HISTORY("history", "История", Icons.Default.CarRepair, "history"),
    PROFILE("profile", "Профиль", Icons.Default.Person, "profile")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        composable(Destination.HOME.route) {
            HomeScreen(
                onBottomBarVisibilityChange = onBottomBarVisibilityChange,
                modifier = modifier
            )
        }

        composable(Destination.HISTORY.route) {
            NotificationScreen(
                modifier = modifier
            )
        }

        composable(Destination.PROFILE.route) {
            ProfileScreen(
                modifier = modifier,
            )
        }
    }
}