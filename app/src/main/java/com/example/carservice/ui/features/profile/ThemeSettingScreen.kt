package com.example.carservice.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carservice.domain.model.ThemeMode
import com.example.carservice.ui.theme.MainTheme
import com.example.carservice.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeSettingsScreen(
    themeViewModel: ThemeViewModel = koinViewModel(),
    onBackPressed: () -> Unit
) {
    val themeState by themeViewModel.state.collectAsState()
    val currentTheme = themeState.currentTheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Шапка без фона (как в Гараже)
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()

                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Назад",
                    tint = MainTheme.colors.mainColor
                )
            }
            Text(
                text = "Тема оформления",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Выберите режим отображения приложения",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            // Контейнер для выбора темы
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MainTheme.colors.navigationBar,
                shadowElevation = 2.dp
            ) {
                Column {
                    ThemeOptionItem(
                        title = "Светлая",
                        icon = Icons.Default.LightMode,
                        isSelected = currentTheme == ThemeMode.Light,
                        onClick = { themeViewModel.setThemeMode(ThemeMode.Light) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    ThemeOptionItem(
                        title = "Тёмная",
                        icon = Icons.Default.DarkMode,
                        isSelected = currentTheme == ThemeMode.Dark,
                        onClick = { themeViewModel.setThemeMode(ThemeMode.Dark) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    ThemeOptionItem(
                        title = "Системная",
                        icon = Icons.Default.SettingsBrightness,
                        isSelected = currentTheme == ThemeMode.System,
                        onClick = { themeViewModel.setThemeMode(ThemeMode.System) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Информационный блок
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MainTheme.colors.mainColor.copy(alpha = 0.05f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SettingsBrightness,
                        contentDescription = null,
                        tint = MainTheme.colors.mainColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Системная тема подстраивается под настройки вашего устройства",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeOptionItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
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
                    .background(
                        color = if (isSelected) MainTheme.colors.mainColor.copy(alpha = 0.1f) 
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MainTheme.colors.mainColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
        
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MainTheme.colors.mainColor
            )
        )
    }
}
