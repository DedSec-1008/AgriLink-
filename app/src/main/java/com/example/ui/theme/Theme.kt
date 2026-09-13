package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = BaseAgriGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = BaseAgriGreenContainer,
  onPrimaryContainer = BaseAgriOnGreenContainer,
  secondary = BaseAgriGoldSecondary,
  onSecondary = Color.White,
  secondaryContainer = BaseAgriGoldContainer,
  onSecondaryContainer = BaseAgriOnGoldContainer,
  tertiary = BaseAgriEarthTertiary,
  onTertiary = Color.White,
  tertiaryContainer = BaseAgriEarthContainer,
  background = BaseAgriBackground,
  onBackground = Color(0xFF191C19),
  surface = BaseAgriSurface,
  onSurface = Color(0xFF191C19),
  surfaceVariant = BaseAgriSurfaceVariant,
  onSurfaceVariant = Color(0xFF43483E),
  outline = BaseAgriOutline,
  outlineVariant = BaseAgriDivider,
  error = BaseAgriError,
  onError = Color.White,
  errorContainer = BaseAgriErrorContainer,
  onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
  primary = DarkAgriGreenPrimary,              // 0xFF81C784 (vibrant leaf green, 8.5:1 contrast)
  onPrimary = Color(0xFF05330D),               // dark green on primary button (7.2:1 contrast)
  primaryContainer = DarkAgriGreenContainer,   // 0xFF1D3B20 (rich green container for badges/chips)
  onPrimaryContainer = DarkAgriOnGreenContainer,// 0xFFA5D6A7 (bright mint text, 7.2:1 contrast)
  secondary = DarkAgriGoldSecondary,           // 0xFFFFB74D (radiant harvest amber, AAA contrast)
  onSecondary = Color(0xFF452200),             // dark brown on gold
  secondaryContainer = DarkAgriGoldContainer,  // 0xFF3B280E (warm dark amber container)
  onSecondaryContainer = DarkAgriOnGoldContainer,// 0xFFFFD599 (golden wheat text, 8.5:1 contrast)
  tertiary = DarkAgriEarthTertiary,            // 0xFFD7CCC8 (soft earth)
  onTertiary = Color(0xFF3E2723),
  tertiaryContainer = DarkAgriEarthContainer,  // 0xFF3E2E2A
  onTertiaryContainer = Color(0xFFEFEBE9),
  background = DarkAgriBackground,             // 0xFF121612 (deep calm forest canvas)
  onBackground = DarkAgriTextPrimary,          // 0xFFF1F5EC (crisp readable off-white, 14:1 contrast)
  surface = DarkAgriSurface,                   // 0xFF1C241C (elevated card surface, clear tonal separation)
  onSurface = DarkAgriTextPrimary,             // 0xFFF1F5EC (crisp readable off-white, 13:1 contrast)
  surfaceVariant = DarkAgriSurfaceVariant,     // 0xFF242F24 (inner container/deduction box)
  onSurfaceVariant = DarkAgriTextSecondary,    // 0xFFBAC7B6 (readable sage-slate, 7.5:1 contrast)
  outline = DarkAgriOutline,                   // 0xFF3E4D3D (card boundary)
  outlineVariant = DarkAgriDivider,            // 0xFF2C392B (dividers)
  error = DarkAgriError,                       // 0xFFFFB4AB (high-contrast error red)
  onError = Color(0xFF690005),
  errorContainer = DarkAgriErrorContainer,     // 0xFF4E0002
  onErrorContainer = Color(0xFFFFDAD6)
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

  val agriColors = if (darkTheme) DarkAgriColors else LightAgriColors

  CompositionLocalProvider(
    LocalAgriColors provides agriColors
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}

