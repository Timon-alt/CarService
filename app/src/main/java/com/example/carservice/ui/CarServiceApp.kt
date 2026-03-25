package com.example.carservice.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carservice.ui.theme.MainTheme

@Composable
fun CarServiceApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME

    // Состояние для управления видимостью BottomBar
    var isBottomBarVisible by remember { mutableStateOf(true) }

    // Используем currentBackStackEntryAsState для отслеживания текущего маршрута
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Определяем выбранный пункт на основе текущего маршрута
    val selectedDestination = Destination.entries.find { it.route == currentRoute }?.ordinal
        ?: startDestination.ordinal

    Scaffold(
        modifier = modifier,
        containerColor = MainTheme.colors.singleTheme,
        bottomBar = {
            // Показываем BottomBar только если он видим и мы на нужных экранах
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
                modifier = modifier
            )
        }
    }
}