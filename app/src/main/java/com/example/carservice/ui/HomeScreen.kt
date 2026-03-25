package com.example.carservice.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.carservice.R
import com.example.carservice.ui.theme.MainTheme

@Composable
fun HomeScreen(
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeNavController = rememberNavController()

    // Следим за изменениями в навигации внутри HomeScreen
    val currentRoute = homeNavController.currentBackStackEntryAsState().value?.destination?.route

    // Обновляем видимость BottomBar в зависимости от текущего маршрута
    DisposableEffect(currentRoute) {
        // Скрываем BottomBar на экране услуги, показываем на главном
        val shouldShowBottomBar = currentRoute == HomeRoutes.Main.route
        onBottomBarVisibilityChange(shouldShowBottomBar)

        onDispose { }
    }

    HomeScreenNavHost(
        navController = homeNavController,
        onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        modifier = modifier
    )
}

@Composable
fun HomeMainScreen(
    onNavigateToService: () -> Unit,
    onNavigateToCarMaintenance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 25.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Главная",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Уведомления"
            )
        }

        Column {
            Text(text = "Услуги")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(6) {
                    Card(
                        modifier = Modifier.clickable {
                            onNavigateToService()
                        }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.shina),
                            contentDescription = "Услуга"
                        )
                        Text(text = "Шиномонтаж")
                    }
                }
            }
        }

        Button(
            onClick = { onNavigateToService() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(50.dp).fillMaxWidth()
        ) {
            Text(text = "Записаться на ТО")
        }
    }
}

@Composable
fun HomeScreenNavHost(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoutes.Main.route
    ) {
        composable(HomeRoutes.Main.route) {
            HomeMainScreen(
                onNavigateToService = {
                    navController.navigate(HomeRoutes.Service.route)
                },
                onNavigateToCarMaintenance = {
                    navController.navigate(HomeRoutes.CarMaintenance.route)
                },
                modifier = modifier
            )
        }

        composable(HomeRoutes.CarMaintenance.route) {
            CarMaintenanceScreen(
                onBack = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }

        composable(HomeRoutes.Service.route) {
            ServiceScreen(
                onBack = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}

sealed class HomeRoutes(val route: String) {
    object Main : HomeRoutes("home_main")
    object CarMaintenance : HomeRoutes("car_maintenance")
    object Service : HomeRoutes("service")
}