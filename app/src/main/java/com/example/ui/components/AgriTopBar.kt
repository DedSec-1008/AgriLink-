package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppLanguage
import com.example.data.createLocalizedContext
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

/**
 * Top App Bar for KisanSetu:
 * - Respects system status-bar insets (statusBarsPadding)
 * - KisanSetu branding and logo
 * - Language switcher with immediate accessibility and 48dp+ touch target
 * - Location indicator
 * - Profile avatar and details
 * - Help shortcut
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgriTopBar(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onHelpClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    onProfileClicked: (() -> Unit)? = null
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (isCompact) 6.dp else 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Branding Logo + Title + Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 36.dp else 42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = stringResource(R.string.app_name),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = if (isCompact) 18.sp else 21.sp,
                            lineHeight = 24.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AgriTextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = stringResource(R.string.location_nagpur),
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: Language Selector + Help + Profile
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language selector pill (>=48dp touch target)
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(AgriGreenContainer)
                                .border(1.dp, AgriHeroGreenBorder, RoundedCornerShape(24.dp))
                                .clickable { languageMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("topbar_language_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false }
                        ) {
                            AppLanguage.values().forEach { language ->
                                val isSelected = language == currentLanguage
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = language.displayName,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) AgriGreenPrimary else AgriTextPrimary
                                            )
                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = AgriGreenPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        languageMenuExpanded = false
                                        onLanguageSelected(language)
                                    }
                                )
                            }
                        }
                    }

                    // Help Shortcut (>=48dp touch target)
                    IconButton(
                        onClick = onHelpClicked,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("topbar_help_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = stringResource(R.string.nav_help),
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Profile Shortcut (>=48dp touch target)
                    IconButton(
                        onClick = {
                            if (onProfileClicked != null) {
                                onProfileClicked()
                            } else {
                                showProfileDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("topbar_profile_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AgriGreenContainer)
                                .border(1.dp, AgriHeroGreenBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.topbar_profile_desc),
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        FarmerProfileDialog(
            currentLanguage = currentLanguage,
            onDismissRequest = { showProfileDialog = false }
        )
    }
}

/**
 * Farmer Profile Dialog:
 * Displays farmer details, location, FPO membership, and helpline contact info.
 * Dynamically reacts to the active application language (English, Hindi, Marathi).
 * Ensures accessible font scaling (100% - 150%) and high contrast in light/dark themes.
 */
@Composable
fun FarmerProfileDialog(
    currentLanguage: AppLanguage,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val localizedContext = remember(currentLanguage, context) {
        context.createLocalizedContext(currentLanguage.code)
    }

    val farmerRole = localizedContext.getString(R.string.profile_farmer_role)
    val farmerName = localizedContext.getString(R.string.profile_farmer_name)
    val farmerTitle = localizedContext.getString(R.string.profile_farmer_name_with_role, farmerName, farmerRole)
    val locationText = localizedContext.getString(R.string.location_nagpur)
    val fpoName = localizedContext.getString(R.string.profile_fpo_name)
    val fpoInfo = localizedContext.getString(R.string.profile_fpo_label, fpoName)
    val memberId = localizedContext.getString(R.string.profile_member_id_label, "FPO-NGP-2024-8841")
    val helpline = localizedContext.getString(R.string.profile_helpline_format, "1800-180-1551")
    val okBtn = localizedContext.getString(R.string.btn_ok)
    val avatarDesc = localizedContext.getString(R.string.profile_avatar_desc)

    CompositionLocalProvider(LocalContext provides localizedContext) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier
                .testTag("farmer_profile_dialog")
                .widthIn(max = 440.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = avatarDesc,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = farmerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("profile_farmer_name")
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = locationText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("profile_farmer_location")
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = fpoInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("profile_fpo_info")
                    )
                    Text(
                        text = memberId,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("profile_member_id")
                    )
                    Text(
                        text = helpline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("profile_helpline")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .testTag("profile_dialog_ok_btn")
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = okBtn,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}
