package com.example.carservice.ui.features.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carservice.data.carServiceList

@Composable
fun HomeScreen(
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeNavController = rememberNavController()

    // Следим за изменениями в навигации внутри HomeScreen
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
    onNavigateToService: (String) -> Unit,
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

        // Рекламный баннер
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(id = com.example.carservice.R.drawable.avtomasterskaya_example),
                    contentDescription = "Рекламный баннер",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Можно добавить текст поверх баннера
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Лучший сервис\nдля вашего авто",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier
                            .background(
                                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Column {
            Text(
                text = "Услуги",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(
                    items = carServiceList,
                    key = { it.id }){ service ->
                    CarServiceCard(
                        name = service.name,
                        image = service.image,
                        onNavigateToService = { onNavigateToService(service.name) }
                        )
                }
            }
        }

        Button(
            onClick = { onNavigateToCarMaintenance() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(50.dp).fillMaxWidth()
        ) {
            Text(text = "Записаться на ТО")
        }
    }
}

@Composable
fun CarServiceCard(name: String, image: Int, onNavigateToService: () -> Unit,) {
    Card(
        modifier = Modifier
            .width(125.dp)
            .clickable { onNavigateToService() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Единый контейнер для всех изображений
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Фиксированная высота
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = name,
                    contentScale = ContentScale.Crop, // Обрезает до размеров контейнера
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = name,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
        startDestination = HomeRoutes.Main.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        animationSpec = tween(300),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        animationSpec = tween(300),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideIntoContainer(
                        animationSpec = tween(300),
                        towards = AnimatedContentTransitionScope.SlideDirection.End
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutOfContainer(
                        animationSpec = tween(300),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
        }
    ) {
        composable(HomeRoutes.Main.route) {
            HomeMainScreen(
                onNavigateToService = { servicName ->
                    navController.navigate(HomeRoutes.Service.passName(servicName))
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
                    navController.navigateUp()
                },
                modifier = modifier
            )
        }

        composable(
            route = "service/{category}",
            arguments = listOf(
                navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            // Получаем название услуги из аргументов
            val category = backStackEntry.arguments?.getString("category") ?: ""
            ServiceScreen(
                category = category,
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
    object Service : HomeRoutes("service/{serviceName}") {
        fun passName(serviceName: String): String = "service/$serviceName"
    }

}
