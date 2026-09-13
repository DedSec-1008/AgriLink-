package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AgriGoldSecondary
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.unit.Dp

enum class FarmerNavDestination(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    PRICES(R.string.nav_prices, Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
    SELL(R.string.nav_sell, Icons.Filled.Sell, Icons.Filled.Sell),
    MY_LOTS(R.string.nav_my_lots, Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    HELP(R.string.nav_help, Icons.AutoMirrored.Filled.HelpOutline, Icons.AutoMirrored.Outlined.HelpOutline)
}

/**
 * Redesigned Farmer Bottom Navigation:
 * - All 5 primary destinations preserved
 * - Clear, high-contrast visual indicators
 * - Tactile Sell accent without clutter
 * - Fully accessible at large font scales (100% - 150%)
 */
@Composable
fun FarmerBottomNavigation(
    currentDestination: FarmerNavDestination,
    onNavigate: (FarmerNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
        NavigationBar(
            modifier = Modifier
                .shadow(8.dp)
                .heightIn(min = 68.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            FarmerNavDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination
                val isSell = destination == FarmerNavDestination.SELL

                if (isSell) {
                    // Prominent center SELL action
                    val sellBg = if (isSelected) AgriGoldSecondary else MaterialTheme.colorScheme.primary
                    val sellTint = if (isSelected) {
                        if (isDark) Color(0xFF452200) else Color.White
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigate(destination) },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        color = sellBg,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = destination.selectedIcon,
                                    contentDescription = stringResource(destination.titleRes),
                                    tint = sellTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleRes),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) AgriGoldSecondary else MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                    )
                } else {
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigate(destination) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.titleRes),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleRes),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = AgriGreenContainer,
                            unselectedIconColor = AgriTextSecondary,
                            unselectedTextColor = AgriTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                    )
                }
            }
        }
    }
}

/**
 * Adaptive Navigation Rail for landscape and tablet orientations.
 */
@Composable
fun FarmerNavigationRail(
    currentDestination: FarmerNavDestination,
    onNavigate: (FarmerNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier.shadow(6.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = AgriTextPrimary,
        header = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            FarmerNavDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination
                val isSell = destination == FarmerNavDestination.SELL

                NavigationRailItem(
                    selected = isSelected,
                    onClick = { onNavigate(destination) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = stringResource(destination.titleRes),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(destination.titleRes),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = if (isSell) AgriGoldSecondary else MaterialTheme.colorScheme.primary,
                        selectedTextColor = if (isSell) AgriGoldSecondary else MaterialTheme.colorScheme.primary,
                        indicatorColor = AgriGreenContainer,
                        unselectedIconColor = AgriTextSecondary,
                        unselectedTextColor = AgriTextSecondary
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .testTag("nav_item_${destination.name.lowercase()}")
                )
            }
        }
    }
}
