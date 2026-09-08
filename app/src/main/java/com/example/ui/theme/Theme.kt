package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = AgriGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = AgriGreenContainer,
  onPrimaryContainer = AgriOnGreenContainer,
  secondary = AgriGoldSecondary,
  onSecondary = Color.White,
  secondaryContainer = AgriGoldContainer,
  onSecondaryContainer = AgriOnGoldContainer,
  tertiary = AgriEarthTertiary,
  onTertiary = Color.White,
  tertiaryContainer = AgriEarthContainer,
  background = AgriBackground,
  onBackground = Color(0xFF191C19),
  surface = AgriSurface,
  onSurface = Color(0xFF191C19),
  surfaceVariant = AgriSurfaceVariant,
  onSurfaceVariant = Color(0xFF43483E),
  outline = AgriOutline,
  error = AgriError,
  errorContainer = AgriErrorContainer
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF81C784),
  onPrimary = Color(0xFF00390B),
  primaryContainer = AgriGreenPrimary,
  onPrimaryContainer = Color(0xFFA5D6A7),
  secondary = Color(0xFFFFB74D),
  onSecondary = Color(0xFF4E2600),
  secondaryContainer = AgriGoldSecondary,
  onSecondaryContainer = Color(0xFFFFE082),
  background = Color(0xFF111411),
  onBackground = Color(0xFFE1E4DD),
  surface = Color(0xFF191C19),
  onSurface = Color(0xFFE1E4DD),
  surfaceVariant = Color(0xFF43483E),
  onSurfaceVariant = Color(0xFFC4C9BB)
)

@Composable
fun AgriLinkTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve branded agricultural palette by default
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
