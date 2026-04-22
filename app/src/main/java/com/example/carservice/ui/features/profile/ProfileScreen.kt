package com.example.carservice.ui.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.carservice.ui.features.auth.AuthViewModel
import com.example.carservice.ui.theme.MainTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    ProfileNavHost(modifier = modifier)
}

@Composable
fun ProfileMainScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel, // Передаем общую ViewModel
    onNavigateToThemeSettings: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToCars: () -> Unit = {},
) {
    val authViewModel: AuthViewModel = koinViewModel()
    val uiState by profileViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        Text(
            text = "Профиль",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Блок пользователя
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MainTheme.colors.navigationBar,
            shadowElevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp), color = MainTheme.colors.mainColor)
                } else {
                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Аватар",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${uiState.customer?.firstName ?: "Пользователь"} ${uiState.customer?.lastName ?: ""}".trim(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Секция настроек
        Text(
            text = "Настройки",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MainTheme.colors.navigationBar,
            shadowElevation = 1.dp
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Изменить данные",
                    onClick = onNavigateToEditProfile
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Default.Palette,
                    title = "Изменить тему",
                    onClick = onNavigateToThemeSettings
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Default.DirectionsCar,
                    title = "Автомобили",
                    onClick = onNavigateToCars
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка выхода
        TextButton(
            onClick = { authViewModel.signOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Выйти из Аккаунта", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MainTheme.colors.mainColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MainTheme.colors.mainColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(16.dp)
        )
    }
}

sealed class ProfileDestination(val route: String) {
    object Main : ProfileDestination("profile_main")
    object ThemeSettings : ProfileDestination("profile_theme_settings")
    object Garage : ProfileDestination("profile_garage")
    object EditProfile : ProfileDestination("edit_profile")
}

@Composable
fun ProfileNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ProfileDestination.Main.route
) {
    // Получаем ViewModel ОДИН РАЗ здесь, чтобы она была общей для всех дочерних экранов
    val profileViewModel: ProfileViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ProfileDestination.Main.route) {
            ProfileMainScreen(
                profileViewModel = profileViewModel, // Передаем
                onNavigateToThemeSettings = { navController.navigate(ProfileDestination.ThemeSettings.route) },
                onNavigateToCars = { navController.navigate(ProfileDestination.Garage.route) },
                onNavigateToEditProfile = { navController.navigate(ProfileDestination.EditProfile.route) }
            )
        }

        composable(ProfileDestination.ThemeSettings.route) {
            ThemeSettingsScreen(onBackPressed = { navController.popBackStack() })
        }
        
        composable(ProfileDestination.Garage.route) {
            GarageScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(ProfileDestination.EditProfile.route) {
            EditProfileScreen(
                viewModel = profileViewModel, // Передаем ту же самую ViewModel
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
