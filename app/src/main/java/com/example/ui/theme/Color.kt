package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// BASE LIGHT THEME CONSTANTS (Preserved exactly for Light Mode)
// ============================================================================
val BaseAgriGreenPrimary = Color(0xFF1B5E20)       // Deep Forest/Kisan Green
val BaseAgriGreenDark = Color(0xFF0E3D14)          // Rich Deep Earth Green
val BaseAgriGreenLight = Color(0xFF2E7D32)         // Growth Green
val BaseAgriGreenContainer = Color(0xFFE8F5E9)     // Soft Green Surface
val BaseAgriOnGreenContainer = Color(0xFF00390B)
val BaseAgriGreenText = Color(0xFF1B5E20)

val BaseAgriGoldSecondary = Color(0xFFE65100)      // Deep Warm Harvest Amber
val BaseAgriGoldLight = Color(0xFFFFB300)          // Ripe Wheat Gold
val BaseAgriGoldContainer = Color(0xFFFFF8E1)      // Warm Sunlit Tint
val BaseAgriOnGoldContainer = Color(0xFF552800)
val BaseAgriGoldText = Color(0xFFC67D00)

val BaseAgriEarthTertiary = Color(0xFF5D4037)      // Rich Soil
val BaseAgriEarthContainer = Color(0xFFEFEBE9)

val BaseAgriBackground = Color(0xFFFBFBF6)         // Soft warm natural canvas
val BaseAgriSurface = Color(0xFFFFFFFF)            // Clean white card surface
val BaseAgriSurfaceVariant = Color(0xFFF1F5EC)     // Soft light green-tinted container
val BaseAgriOutline = Color(0xFFC2CCBD)
val BaseAgriDivider = Color(0xFFE0E7DC)

val BaseAgriTextPrimary = Color(0xFF141A13)        // Deepest charcoal-green (14:1 contrast on white)
val BaseAgriTextSecondary = Color(0xFF2E382B)      // Dark moss-slate (8:1 contrast on white)
val BaseAgriTextMuted = Color(0xFF4A5546)          // Clear olive slate for secondary labels
val BaseAgriCardBorder = Color(0xFFD2DCC7)         // Crisp edge border for outdoor screen glare
val BaseAgriHeroGreenBorder = Color(0xFFA5D6A7)    // Vibrant container edge

val BaseAgriSuccess = Color(0xFF2E7D32)
val BaseAgriWarning = Color(0xFFED6C02)
val BaseAgriInfo = Color(0xFF0288D1)
val BaseAgriError = Color(0xFFBA1A1A)
val BaseAgriErrorContainer = Color(0xFFFFDAD6)

// ============================================================================
// CALIBRATED DARK THEME CONSTANTS (Accessible, High Contrast, Agricultural)
// ============================================================================
val DarkAgriGreenPrimary = Color(0xFF81C784)       // Vibrant Leaf Green (8.5:1 on dark surface)
val DarkAgriGreenDark = Color(0xFF163319)          // Dark forest accent
val DarkAgriGreenLight = Color(0xFFA5D6A7)         // Bright mint
val DarkAgriGreenContainer = Color(0xFF1D3B20)     // Rich green badge/chip container
val DarkAgriOnGreenContainer = Color(0xFFA5D6A7)   // Mint text on green container (>7:1 contrast)
val DarkAgriGreenText = Color(0xFF81C784)          // High contrast green text

val DarkAgriGoldSecondary = Color(0xFFFFB74D)      // Radiant harvest amber (WCAG AAA contrast)
val DarkAgriGoldLight = Color(0xFFFFD54F)
val DarkAgriGoldContainer = Color(0xFF3B280E)      // Warm dark amber badge container
val DarkAgriOnGoldContainer = Color(0xFFFFD599)    // Golden wheat text on amber container
val DarkAgriGoldText = Color(0xFFFFB74D)

val DarkAgriEarthTertiary = Color(0xFFD7CCC8)
val DarkAgriEarthContainer = Color(0xFF3E2E2A)

val DarkAgriBackground = Color(0xFF121612)         // Calm, forest-tinted dark canvas (NOT pitch black)
val DarkAgriSurface = Color(0xFF1C241C)            // Distinct elevated card surface (~20% lightness)
val DarkAgriSurfaceVariant = Color(0xFF242F24)     // Inner box / deduction container (~25% lightness)
val DarkAgriOutline = Color(0xFF3E4D3D)            // Clean card boundary
val DarkAgriDivider = Color(0xFF2C392B)

val DarkAgriTextPrimary = Color(0xFFF1F5EC)        // Crisp, high-contrast off-white (13:1 contrast)
val DarkAgriTextSecondary = Color(0xFFBAC7B6)      // Legible sage-slate (7.5:1 contrast)
val DarkAgriTextMuted = Color(0xFF90A18C)          // Readable muted text (5.1:1 contrast)
val DarkAgriCardBorder = Color(0xFF334232)         // Clear card separation
val DarkAgriHeroGreenBorder = Color(0xFF4C7D4E)    // Emerald edge for hero opportunity

val DarkAgriSuccess = Color(0xFF81C784)
val DarkAgriWarning = Color(0xFFFFB74D)
val DarkAgriInfo = Color(0xFF64B5F6)
val DarkAgriError = Color(0xFFFFB4AB)
val DarkAgriErrorContainer = Color(0xFF4E0002)

// ============================================================================
// THEME-AWARE AGRI COLOR PALETTE
// ============================================================================
@Immutable
data class AgriCustomColors(
    val greenPrimary: Color,
    val greenDark: Color,
    val greenLight: Color,
    val greenContainer: Color,
    val onGreenContainer: Color,
    val greenText: Color,
    val goldSecondary: Color,
    val goldLight: Color,
    val goldContainer: Color,
    val onGoldContainer: Color,
    val goldText: Color,
    val earthTertiary: Color,
    val earthContainer: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val cardBorder: Color,
    val heroGreenBorder: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val error: Color,
    val errorContainer: Color,
    val isDark: Boolean
)

val LightAgriColors = AgriCustomColors(
    greenPrimary = BaseAgriGreenPrimary,
    greenDark = BaseAgriGreenDark,
    greenLight = BaseAgriGreenLight,
    greenContainer = BaseAgriGreenContainer,
    onGreenContainer = BaseAgriOnGreenContainer,
    greenText = BaseAgriGreenText,
    goldSecondary = BaseAgriGoldSecondary,
    goldLight = BaseAgriGoldLight,
    goldContainer = BaseAgriGoldContainer,
    onGoldContainer = BaseAgriOnGoldContainer,
    goldText = BaseAgriGoldText,
    earthTertiary = BaseAgriEarthTertiary,
    earthContainer = BaseAgriEarthContainer,
    background = BaseAgriBackground,
    surface = BaseAgriSurface,
    surfaceVariant = BaseAgriSurfaceVariant,
    outline = BaseAgriOutline,
    divider = BaseAgriDivider,
    textPrimary = BaseAgriTextPrimary,
    textSecondary = BaseAgriTextSecondary,
    textMuted = BaseAgriTextMuted,
    cardBorder = BaseAgriCardBorder,
    heroGreenBorder = BaseAgriHeroGreenBorder,
    success = BaseAgriSuccess,
    warning = BaseAgriWarning,
    info = BaseAgriInfo,
    error = BaseAgriError,
    errorContainer = BaseAgriErrorContainer,
    isDark = false
)

val DarkAgriColors = AgriCustomColors(
    greenPrimary = DarkAgriGreenPrimary,
    greenDark = DarkAgriGreenDark,
    greenLight = DarkAgriGreenLight,
    greenContainer = DarkAgriGreenContainer,
    onGreenContainer = DarkAgriOnGreenContainer,
    greenText = DarkAgriGreenText,
    goldSecondary = DarkAgriGoldSecondary,
    goldLight = BaseAgriGoldLight,
    goldContainer = DarkAgriGoldContainer,
    onGoldContainer = DarkAgriOnGoldContainer,
    goldText = DarkAgriGoldText,
    earthTertiary = DarkAgriEarthTertiary,
    earthContainer = DarkAgriEarthContainer,
    background = DarkAgriBackground,
    surface = DarkAgriSurface,
    surfaceVariant = DarkAgriSurfaceVariant,
    outline = DarkAgriOutline,
    divider = DarkAgriDivider,
    textPrimary = DarkAgriTextPrimary,
    textSecondary = DarkAgriTextSecondary,
    textMuted = DarkAgriTextMuted,
    cardBorder = DarkAgriCardBorder,
    heroGreenBorder = DarkAgriHeroGreenBorder,
    success = DarkAgriSuccess,
    warning = DarkAgriWarning,
    info = DarkAgriInfo,
    error = DarkAgriError,
    errorContainer = DarkAgriErrorContainer,
    isDark = true
)

val LocalAgriColors = staticCompositionLocalOf { LightAgriColors }

// ============================================================================
// DYNAMIC COMPOSABLE ACCESSORS (Transparently theme-aware across all screens)
// ============================================================================
val AgriGreenPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.greenPrimary

val AgriGreenDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.greenDark

val AgriGreenLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.greenLight

val AgriGreenContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.greenContainer

val AgriOnGreenContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.onGreenContainer

val AgriGreenText: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.greenText

val AgriGoldSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.goldSecondary

val AgriGoldLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.goldLight

val AgriGoldContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.goldContainer

val AgriOnGoldContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.onGoldContainer

val AgriGoldText: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.goldText

val AgriEarthTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.earthTertiary

val AgriEarthContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.earthContainer

val AgriBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.background

val AgriSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.surface

val AgriSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.surfaceVariant

val AgriOutline: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.outline

val AgriDivider: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.divider

val AgriTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.textPrimary

val AgriTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.textSecondary

val AgriTextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.textMuted

val AgriCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.cardBorder

val AgriHeroGreenBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.heroGreenBorder

val AgriSuccess: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.success

val AgriWarning: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.warning

val AgriInfo: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.info

val AgriError: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.error

val AgriErrorContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAgriColors.current.errorContainer

