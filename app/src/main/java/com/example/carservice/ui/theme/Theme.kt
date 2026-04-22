package com.example.carservice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.carservice.domain.model.ThemeMode

data class ColorPalette(
    val mainColor: Color,
    val singleTheme: Color,
    val oppositeTheme: Color,
    val navigationBar: Color,
    val onMainColor: Color // Новый цвет специально для текста поверх основного цвета
)

val baseLightPalette = ColorPalette(
    mainColor = MainColor,
    singleTheme = LightBack,
    oppositeTheme = DarkBack,
    navigationBar = Color(0xFFFFFFFF),
    onMainColor = Color.White // В светлой теме текст на основной кнопке будет белым
)

val baseDarkPalette = ColorPalette(
    mainColor = MainColor,
    singleTheme = DarkBack,
    oppositeTheme = LightBack,
    navigationBar = Color(0xFF1C1C1E),
    onMainColor = Color.White // В темной теме тоже оставим белым для контраста
)

val LocalColors = staticCompositionLocalOf<ColorPalette> {
    error("Colors composition error")
}

@Composable
fun MainTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    val colors = if (!darkTheme) baseLightPalette else baseDarkPalette

    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.mainColor,
            onPrimary = colors.onMainColor, // Белый текст на кнопках
            surface = colors.singleTheme,
            background = colors.singleTheme,
            onSurface = colors.oppositeTheme,
            onBackground = colors.oppositeTheme,
            secondaryContainer = Color.Transparent,
            onSecondaryContainer = colors.mainColor
        )
    } else {
        lightColorScheme(
            primary = colors.mainColor,
            onPrimary = colors.onMainColor, // Белый текст на кнопках (исправлено!)
            surface = colors.singleTheme,
            background = colors.singleTheme,
            onSurface = colors.oppositeTheme,
            onBackground = colors.oppositeTheme,
            secondaryContainer = Color(0xFFF5F5F7), // Мягкий фон для контейнеров в светлой теме
            onSecondaryContainer = colors.mainColor
        )
    }

    CompositionLocalProvider(
        LocalColors provides colors,
        content = {
            MaterialTheme(
                colorScheme = materialColorScheme,
                typography = Typography,
                content = content
            )
        }
    )
}

object MainTheme {
    val colors: ColorPalette
    @Composable @ReadOnlyComposable
    get() = LocalColors.current
}
