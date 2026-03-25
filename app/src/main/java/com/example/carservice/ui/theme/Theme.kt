package com.example.carservice.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class ColorPalette(
    val mainColor: Color,
    val singleTheme: Color,
    val oppositeTheme: Color,
    val navigationBar: Color
)

val baseLightPalette = ColorPalette(
    mainColor = MainColor,
    singleTheme = LightBack,
    oppositeTheme = DarkBack,
    navigationBar = Color(0xFFFFFFFF)
)

val baseDarkPalette = ColorPalette(
    mainColor = MainColor,
    singleTheme = DarkBack,
    oppositeTheme = LightBack,
    navigationBar = Color(0xFF1C1C1E)
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val LocalColors = staticCompositionLocalOf<ColorPalette> {
    error("Colors composition error")
}

@Composable
fun MainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Выбираем нужную палитру (ваш код)
    val colors = if (!darkTheme) baseLightPalette else baseDarkPalette

    // Создаем MaterialColorScheme на основе нашей палитры
    val materialColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.mainColor,
            surface = colors.singleTheme,       // Явно говорим: "фон Surface = singleTheme"
            background = colors.singleTheme,    // Явно говорим: "фон экрана = singleTheme"
            onPrimary = colors.oppositeTheme,   // Цвет текста на primary
            onSurface = colors.oppositeTheme,    // Цвет текста на surface
            onSecondaryContainer = colors.mainColor,
            secondaryContainer = Color.Transparent

        )
    } else {
        lightColorScheme(
            primary = colors.mainColor,
            surface = colors.singleTheme,
            background = colors.singleTheme,
            onPrimary = colors.oppositeTheme,
            onSurface = colors.oppositeTheme,
            onSecondaryContainer = colors.mainColor,
            secondaryContainer = Color.Transparent
        )
    }

    // Предоставляем ДВЕ темы одновременно:
    // 1. Нашу кастомную (LocalColors) для доступа через MainTheme.colors
    // 2. MaterialTheme для всех стандартных компонентов
    CompositionLocalProvider(
        LocalColors provides colors,
        content = {
            MaterialTheme(
                colorScheme = materialColorScheme,
                content = content
            )
        }
    )
}

@Composable
fun CarServiceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

object MainTheme {
    val colors: ColorPalette
    @Composable @ReadOnlyComposable
    get() = LocalColors.current
}