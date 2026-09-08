package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AgriGoldSecondary
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

enum class FarmerNavDestination(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    PRICES(R.string.nav_prices, Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
    SELL(R.string.nav_sell, Icons.Filled.Sell, Icons.Filled.Sell),
    MY_LOTS(R.string.nav_my_lots, Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    HELP(R.string.nav_help, Icons.Filled.HelpOutline, Icons.Outlined.HelpOutline)
}

@Composable
fun FarmerBottomNavigation(
    currentDestination: FarmerNavDestination,
    onNavigate: (FarmerNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.shadow(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        FarmerNavDestination.values().forEach { destination ->
            val isSelected = currentDestination == destination
            val isSell = destination == FarmerNavDestination.SELL

            if (isSell) {
                // Prominent center SELL item - seamlessly integrated, not disconnected
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(destination) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = if (isSelected) AgriGoldSecondary else AgriGreenPrimary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = destination.selectedIcon,
                                contentDescription = stringResource(destination.titleRes),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(destination.titleRes),
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) AgriGoldSecondary else AgriGreenPrimary,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
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
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AgriGreenPrimary,
                        selectedTextColor = AgriGreenPrimary,
                        indicatorColor = AgriGreenContainer,
                        unselectedIconColor = AgriTextSecondary,
                        unselectedTextColor = AgriTextSecondary
                    )
                )
            }
        }
    }
}

/**
 * Adaptive Navigation Rail for landscape orientations.
 * Provides comfortable vertical spacing, >=48dp touch targets, and preserves
 * the exact same 5 farmer destinations with high-contrast visual clarity.
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
                    .background(AgriGreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FarmerNavDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination
                val isSell = destination == FarmerNavDestination.SELL

                if (isSell) {
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { onNavigate(destination) },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        color = if (isSelected) AgriGoldSecondary else AgriGreenPrimary,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = destination.selectedIcon,
                                    contentDescription = stringResource(destination.titleRes),
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleRes),
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) AgriGoldSecondary else AgriGreenPrimary,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationRailItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                } else {
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { onNavigate(destination) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.titleRes),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleRes),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = AgriGreenPrimary,
                            selectedTextColor = AgriGreenPrimary,
                            indicatorColor = AgriGreenContainer,
                            unselectedIconColor = AgriTextSecondary,
                            unselectedTextColor = AgriTextSecondary
                        )
                    )
                }
            }
        }
    }
}
