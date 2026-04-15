package com.example.carservice.ui.features.profile

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carservice.domain.model.ThemeMode
import com.example.carservice.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ThemeSettingsScreen(
    themeViewModel: ThemeViewModel = koinViewModel(),
    onBackPressed: () -> Unit) {

    val themeState by themeViewModel.state.collectAsState()
    val currentTheme = themeState.currentTheme

    val themes = listOf(ThemeMode.Light, ThemeMode.Dark, ThemeMode.System)

    Column() {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            themes.forEach { theme ->
                ThemeButton(
                    text = ThemeMode.getDisplayName(theme),
                    isSelected = currentTheme == theme,
                    onClick = { themeViewModel.setThemeMode(theme) }
                )
            }
        }
        Button(onClick = onBackPressed) { Text(text = "Назад") }
    }
}

@Composable
fun ThemeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(text)
    }
}