package com.example.ui.screens.selling

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.AgriRepository
import com.example.model.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// -------------------------------------------------------------------------
// STEP 2 — QUANTITY (Section 6)
// -------------------------------------------------------------------------
@Composable
fun StepQuantityScreen(
    selectedCrop: CropOption,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showManualInput by rememberSaveable { mutableStateOf(false) }
    var manualInputText by rememberSaveable { mutableStateOf(if (quantity > 0) "$quantity" else "") }

    // Keep manual text in sync if quantity changes via stepper or quick presets
    LaunchedEffect(quantity) {
        if (quantity > 0 && manualInputText != "$quantity") {
            manualInputText = "$quantity"
        }
    }

    val quickPresets = remember { listOf(5, 10, 25, 50, 100) }

    val parsedNum = manualInputText.toIntOrNull()
    val hasManualError = showManualInput && (manualInputText.isEmpty() || parsedNum == null || parsedNum <= 0)
    val errorMessage = when {
        !showManualInput -> null
        manualInputText.isEmpty() -> stringResource(R.string.err_qty_required)
        parsedNum == null || parsedNum <= 0 -> stringResource(R.string.err_qty_greater_than_zero)
        else -> null
    }

    val isContinueEnabled = quantity in 1..2000 && !hasManualError

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeaderWithProgress(
            stepNumber = 2,
            title = stringResource(R.string.step2_title),
            subtitle = stringResource(R.string.step2_subtitle),
            onBack = onBack
        )

        // Context: Selected Crop Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCropVectorIcon(selectedCrop.id),
                            contentDescription = stringResource(selectedCrop.nameRes),
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(selectedCrop.nameRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "~₹${selectedCrop.typicalPrice} / ${stringResource(R.string.unit_quintals)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextMuted
                        )
                    }
                }

                Surface(
                    color = AgriGreenContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.state_selected),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (isLandscape) {
            // Responsive Landscape: 2 balanced columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Left Column: Large Tactile Stepper & Quantity
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (quantity > 0) "$quantity" else "0",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 54.sp),
                            fontWeight = FontWeight.Black,
                            color = if (quantity > 0) AgriGreenPrimary else AgriTextMuted
                        )
                        Text(
                            text = stringResource(R.string.unit_quintals).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Stepper Controls [-] and [+]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    val newQty = when {
                                        quantity > 5 -> quantity - 5
                                        quantity in 2..5 -> 1
                                        else -> 1
                                    }
                                    onQuantityChanged(newQty)
                                    manualInputText = "$newQty"
                                },
                                enabled = quantity > 1,
                                shape = CircleShape,
                                color = if (quantity > 1) AgriGreenContainer else AgriCardBorder.copy(alpha = 0.4f),
                                contentColor = if (quantity > 1) AgriGreenPrimary else AgriTextMuted,
                                modifier = Modifier
                                    .size(56.dp)
                                    .testTag("stepper_decrease")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = stringResource(R.string.desc_decrease_qty),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    val newQty = if (quantity < 1) 5 else minOf(quantity + 5, 2000)
                                    onQuantityChanged(newQty)
                                    manualInputText = "$newQty"
                                },
                                enabled = quantity < 2000,
                                shape = CircleShape,
                                color = if (quantity < 2000) AgriGreenContainer else AgriCardBorder.copy(alpha = 0.4f),
                                contentColor = if (quantity < 2000) AgriGreenPrimary else AgriTextMuted,
                                modifier = Modifier
                                    .size(56.dp)
                                    .testTag("stepper_increase")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.desc_increase_qty),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Column: Presets, Manual Entry & Continue Button
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Presets Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPresets.forEach { preset ->
                            val isSelected = quantity == preset
                            Surface(
                                onClick = {
                                    onQuantityChanged(preset)
                                    manualInputText = "$preset"
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AgriGreenPrimary else AgriGreenContainer,
                                contentColor = if (isSelected) Color.White else AgriGreenPrimary,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AgriGreenPrimary else AgriGreenLight.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("qty_preset_$preset")
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.quick_qty_label, preset),
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Manual Entry Toggle & Field
                    TextButton(
                        onClick = { showManualInput = !showManualInput },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_toggle_manual_qty")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = AgriGreenLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showManualInput) stringResource(R.string.hide_manual_entry) else stringResource(R.string.type_exact_quantity),
                            color = AgriGreenLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showManualInput) {
                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { input ->
                                val clean = input.filter { it.isDigit() }.take(4)
                                manualInputText = clean
                                val num = clean.toIntOrNull()
                                if (num != null && num in 1..2000) {
                                    onQuantityChanged(num)
                                }
                            },
                            label = { Text(stringResource(R.string.label_exact_quintals)) },
                            suffix = {
                                Text(
                                    text = stringResource(R.string.unit_quintals),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AgriTextMuted
                                )
                            },
                            singleLine = true,
                            isError = hasManualError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("manual_qty_field")
                        )

                        if (errorMessage != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onContinue,
                        enabled = isContinueEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .testTag("continue_step2_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AgriGreenPrimary,
                            contentColor = Color.White,
                            disabledContainerColor = AgriCardBorder.copy(alpha = 0.5f),
                            disabledContentColor = AgriTextMuted
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.btn_continue),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Portrait Layout: Unified Stepper Card + Presets + Manual Entry + Continue
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tactile Stepper: [-]  Quantity / Unit  [+]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // [-] Button (56dp x 56dp)
                        Surface(
                            onClick = {
                                val newQty = when {
                                    quantity > 5 -> quantity - 5
                                    quantity in 2..5 -> 1
                                    else -> 1
                                }
                                onQuantityChanged(newQty)
                                manualInputText = "$newQty"
                            },
                            enabled = quantity > 1,
                            shape = CircleShape,
                            color = if (quantity > 1) AgriGreenContainer else AgriCardBorder.copy(alpha = 0.4f),
                            contentColor = if (quantity > 1) AgriGreenPrimary else AgriTextMuted,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("stepper_decrease")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = stringResource(R.string.desc_decrease_qty),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        // Center: Large Number & Unit
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (quantity > 0) "$quantity" else "0",
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 58.sp),
                                fontWeight = FontWeight.Black,
                                color = if (quantity > 0) AgriGreenPrimary else AgriTextMuted
                            )
                            Text(
                                text = stringResource(R.string.unit_quintals).uppercase(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextSecondary,
                                letterSpacing = 1.sp
                            )
                        }

                        // [+] Button (56dp x 56dp)
                        Surface(
                            onClick = {
                                val newQty = if (quantity < 1) 5 else minOf(quantity + 5, 2000)
                                onQuantityChanged(newQty)
                                manualInputText = "$newQty"
                            },
                            enabled = quantity < 2000,
                            shape = CircleShape,
                            color = if (quantity < 2000) AgriGreenContainer else AgriCardBorder.copy(alpha = 0.4f),
                            contentColor = if (quantity < 2000) AgriGreenPrimary else AgriTextMuted,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("stepper_increase")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.desc_increase_qty),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Presets: 5, 10, 25, 50, 100 quintals
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickPresets.forEach { preset ->
                            val isSelected = quantity == preset
                            Surface(
                                onClick = {
                                    onQuantityChanged(preset)
                                    manualInputText = "$preset"
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AgriGreenPrimary else AgriGreenContainer,
                                contentColor = if (isSelected) Color.White else AgriGreenPrimary,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AgriGreenPrimary else AgriGreenLight.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("qty_preset_$preset")
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.quick_qty_label, preset),
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Manual entry toggle
                    TextButton(
                        onClick = { showManualInput = !showManualInput },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("btn_toggle_manual_qty")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = AgriGreenLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showManualInput) stringResource(R.string.hide_manual_entry) else stringResource(R.string.type_exact_quantity),
                            color = AgriGreenLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showManualInput) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { input ->
                                val clean = input.filter { it.isDigit() }.take(4)
                                manualInputText = clean
                                val num = clean.toIntOrNull()
                                if (num != null && num in 1..2000) {
                                    onQuantityChanged(num)
                                }
                            },
                            label = { Text(stringResource(R.string.label_exact_quintals)) },
                            suffix = {
                                Text(
                                    text = stringResource(R.string.unit_quintals),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AgriTextMuted
                                )
                            },
                            singleLine = true,
                            isError = hasManualError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .testTag("manual_qty_field")
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Continue Button (min 52dp height)
            Button(
                onClick = onContinue,
                enabled = isContinueEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("continue_step2_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AgriGreenPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = AgriCardBorder.copy(alpha = 0.5f),
                    disabledContentColor = AgriTextMuted
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.btn_continue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

