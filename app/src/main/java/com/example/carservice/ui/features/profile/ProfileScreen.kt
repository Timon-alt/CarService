package com.example.carservice.ui.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carservice.R
import com.example.carservice.ui.features.profile.ProfileViewModel
import com.example.carservice.ui.features.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    ProfileNavHost(modifier = modifier)
}

@Composable
fun ProfileMainScreen(
    modifier: Modifier = Modifier,
    onNavigateToThemeSettings: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToCars: () -> Unit = {},
) {
    val authViewModel: AuthViewModel = koinViewModel()
    val profileViewModel: ProfileViewModel = koinViewModel()
    val uiState by profileViewModel.uiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 15.dp)
    ) {
        Text(
            text = "Профиль",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )

        // Отображаем индикатор загрузки или данные
        if (uiState.isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp)
                )
                Text(text = "Загрузка...")
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Аватар профиля",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                Column {
                    Text(
                        text = "${uiState.customer?.firstName ?: ""} ${uiState.customer?.lastName ?: ""}".trim(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (!uiState.customer?.phone.isNullOrBlank()) {
                        Text(
                            text = uiState.customer?.phone ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Настройки",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            TextButton(
                onClick = onNavigateToEditProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Изменить данные")
            }

            TextButton(
                onClick = onNavigateToThemeSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Изменить тему")
            }

            TextButton(
                onClick = onNavigateToCars,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Автомобили")
            }

            TextButton(
                onClick = { authViewModel.signOut() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = ""
                )
                Text(text = "Выйти из Аккаунта")
            }
        }
    }
}

sealed class ProfileDestination(val route: String) {
    object Main : ProfileDestination("profile_main")
    object ThemeSettings : ProfileDestination("profile_theme_settings")
    object  Garage : ProfileDestination("profile_garage")
}

@Composable
fun ProfileNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ProfileDestination.Main.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ProfileDestination.Main.route) {
            ProfileMainScreen(
                onNavigateToThemeSettings = {
                    navController.navigate(ProfileDestination.ThemeSettings.route)
                },
                onNavigateToCars = {
                    navController.navigate(ProfileDestination.Garage.route)
                }
            )
        }

        composable(ProfileDestination.ThemeSettings.route) {
            ThemeSettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable(ProfileDestination.Garage.route) {
            GarageScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}