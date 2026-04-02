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
import com.example.carservice.R
import com.example.carservice.ui.features.profile.ProfileViewModel
import com.example.carservice.ui.features.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {

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

                // Отображаем ФИО (имя и фамилию вместе)
                Column {
                    Text(
                        text = "${uiState.customer?.firstName ?: ""} ${uiState.customer?.lastName ?: ""}".trim(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    // Отображаем номер телефона чуть ниже
                    if (!uiState.customer?.phone.isNullOrBlank()) {
                        Text(
                            text = uiState.customer?.phone ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Настройки",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                TextButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Изменить данные")
                }
                TextButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Изменить тему")
                }
                TextButton(
                    onClick = {},
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
}