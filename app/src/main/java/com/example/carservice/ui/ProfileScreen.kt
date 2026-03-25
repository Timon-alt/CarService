package com.example.carservice.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carservice.R

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 15.dp)
    ) {
        Text(
            text = "Профиль",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Text(text = "Имя профиля")
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Настройки",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp)
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
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = ""
                )
                Text(text = "Выйти из приложения")
            }
        }
    }

}