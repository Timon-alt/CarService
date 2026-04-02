package com.example.carservice.ui.features.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import com.example.carservice.domain.model.Service
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ServiceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    category: String
) {
    val navController = rememberNavController()

    val serviceViewModel: ServiceViewModel = koinViewModel(
        parameters = { parametersOf(category) }
    )
    val uiState by serviceViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "service_list",
        modifier = modifier.fillMaxSize(),
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
        composable("service_list") {
            ServiceListScreen(
                category = category,
                uiState = uiState,
                onBack = onBack,
                onServiceClick = { service ->
                    // Передаем весь объект целиком
                    navController.navigate("detail/${service.id}")
                },
                onRetry = { serviceViewModel.loadServices() }
            )
        }

        composable(
            route = "detail/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: return@composable
            // Находим услугу по ID из уже загруженного списка
            val service = uiState.services.find { it.id == serviceId }

            if (service != null) {
                DetailServiceScreen(
                    service = service,
                    onBack = { navController.navigateUp() }
                )
            } else {
                // Если услуга не найдена, можно попробовать загрузить её отдельно
                // или показать ошибку
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Услуга не найдена")
                }
            }
        }
    }
}

@Composable
fun ServiceListScreen(
    category: String,
    uiState: ServiceUiState,
    onBack: () -> Unit,
    onServiceClick: (Service) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 20.dp, top = 15.dp, bottom = 25.dp)
    ) {
        // Шапка только для списка услуг
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Назад"
                )
            }
            Text(text = category)
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск"
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text("Ошибка: ${uiState.error}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRetry) {
                            Text("Повторить")
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(uiState.services) { service ->
                        ServiceCard(
                            service = service,
                            onClick = { onServiceClick(service) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: Service,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(125.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                AsyncImage(
                    model = service.image,
                    contentDescription = service.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = service.name,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}