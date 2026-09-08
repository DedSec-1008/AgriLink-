package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AgriRepository
import com.example.model.CropOption
import com.example.model.SellingOpportunity
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGoldContainer
import com.example.ui.theme.AgriGoldSecondary
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenLight
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriOnGoldContainer
import com.example.ui.theme.AgriOnGreenContainer
import com.example.ui.theme.AgriSurface
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

@Composable
fun SellScreen(
    repository: AgriRepository,
    viewModel: SellingViewModel,
    onNavigateToMyLots: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = uiState.currentStep != SellingStep.CROP) {
        viewModel.goBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgriBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Render step by step
        when (uiState.currentStep) {
            SellingStep.CROP -> StepCropScreen(
                crops = repository.getAvailableCrops(),
                selectedCrop = uiState.selectedCrop,
                onCropSelected = { viewModel.selectCrop(it) }
            )

            SellingStep.QUANTITY -> StepQuantityScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                onQuantityChanged = { viewModel.updateQuantity(it) },
                onContinue = { viewModel.goToStep(SellingStep.QUALITY) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.QUALITY -> StepQualityScreen(
                selectedCrop = uiState.selectedCrop,
                selectedQualityKey = uiState.qualityKey,
                onQualitySelected = { key, resId ->
                    viewModel.selectQuality(key, resId)
                },
                onContinue = { viewModel.goToStep(SellingStep.LOCATION) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.LOCATION -> StepLocationScreen(
                selectedLocation = uiState.location,
                onLocationSelected = { name, resId ->
                    viewModel.selectLocation(name, resId)
                },
                onContinue = { viewModel.goToStep(SellingStep.READY_DATE) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.READY_DATE -> StepReadyDateScreen(
                selectedTiming = uiState.readyTiming,
                onTimingSelected = { timing, resId ->
                    viewModel.selectReadyTiming(timing, resId)
                },
                onContinue = { viewModel.goToStep(SellingStep.REVIEW) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.REVIEW -> StepReviewScreen(
                uiState = uiState,
                onEditStep = { step -> viewModel.goToStep(step) },
                onFindBestOption = { viewModel.startAnalysisAndFindOptions() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.ANALYSIS -> StepAnalysisScreen(
                progressIndex = uiState.analysisProgressIndex
            )

            SellingStep.RECOMMENDATIONS -> StepRecommendationsScreen(
                uiState = uiState,
                onSelectOpportunity = { viewModel.selectOpportunityToSell(it) },
                onEditDetails = { viewModel.goToStep(SellingStep.REVIEW) },
                onRetry = { viewModel.startAnalysisAndFindOptions() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.CONFIRM_SELL -> StepConfirmSellScreen(
                uiState = uiState,
                onConfirm = { viewModel.confirmCreateLot() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.LOT_CREATED -> StepLotCreatedScreen(
                uiState = uiState,
                onViewMyLot = onNavigateToMyLots,
                onDone = {
                    viewModel.resetFlow()
                    onNavigateToHome()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -------------------------------------------------------------------------
// STEP INDICATOR (SUBTLE, Section 4)
// -------------------------------------------------------------------------
@Composable
private fun StepHeaderWithProgress(
    stepNumber: Int,
    totalSteps: Int = 5,
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Subtle step indicator "Step 2 of 5"
            Text(
                text = stringResource(R.string.sell_step_progress, stepNumber),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
            )

            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("step_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.btn_back),
                        tint = AgriGreenPrimary
                    )
                }
            }
        }

        // Dot indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            for (i in 1..totalSteps) {
                Box(
                    modifier = Modifier
                        .size(if (i == stepNumber) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (i <= stepNumber) AgriGreenPrimary else AgriGreenContainer
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -------------------------------------------------------------------------
// STEP 1 — CROP (Section 5)
// -------------------------------------------------------------------------
@Composable
private fun StepCropScreen(
    crops: List<CropOption>,
    selectedCrop: CropOption,
    onCropSelected: (CropOption) -> Unit
) {
    // Section 2: Sell Screen Entry Banner
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(1.5.dp, AgriGreenLight.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .testTag("sell_screen_entry_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.title_where_to_sell).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AgriGreenPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.subtitle_where_to_sell),
                style = MaterialTheme.typography.bodyMedium,
                color = AgriTextSecondary
            )
        }
    }

    StepHeaderWithProgress(
        stepNumber = 1,
        title = stringResource(R.string.step1_title),
        subtitle = stringResource(R.string.step1_subtitle)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        crops.forEach { crop ->
            val isSelected = selectedCrop.id == crop.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onCropSelected(crop) }
                    .testTag("crop_card_${crop.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AgriGreenContainer else AgriSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else AgriGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = crop.emoji, fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stringResource(crop.nameRes),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            Text(
                                text = "~₹${crop.typicalPrice} / ${stringResource(R.string.unit_quintals)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextMuted
                            )
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AgriGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// STEP 2 — QUANTITY (Section 6)
// -------------------------------------------------------------------------
@Composable
private fun StepQuantityScreen(
    selectedCrop: CropOption,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var showManualInput by remember { mutableStateOf(false) }
    var manualInputText by remember { mutableStateOf("$quantity") }

    StepHeaderWithProgress(
        stepNumber = 2,
        title = stringResource(R.string.step2_title),
        subtitle = stringResource(R.string.step2_subtitle),
        onBack = onBack
    )

    // Crop badge
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AgriGreenContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = selectedCrop.emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(selectedCrop.nameRes),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AgriGreenPrimary
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Prominent Stepper Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$quantity",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = AgriGreenPrimary
            )
            Text(
                text = stringResource(R.string.unit_quintals),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tactile Stepper: [-]  [+]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [-] Button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AgriGreenContainer)
                        .clickable { if (quantity > 5) onQuantityChanged(quantity - 5) }
                        .testTag("stepper_decrease"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.desc_decrease_qty),
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Presets
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(25, 50, 100).forEach { preset ->
                        val isCurrent = quantity == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) AgriGreenPrimary else AgriGreenContainer)
                                .clickable { onQuantityChanged(preset) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("qty_preset_$preset")
                        ) {
                            Text(
                                text = "$preset q",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (isCurrent) Color.White else AgriGreenPrimary
                            )
                        }
                    }
                }

                // [+] Button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AgriGreenContainer)
                        .clickable { onQuantityChanged(quantity + 5) }
                        .testTag("stepper_increase"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.desc_increase_qty),
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manual entry toggle
            TextButton(
                onClick = { showManualInput = !showManualInput },
                modifier = Modifier.height(44.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
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
                        manualInputText = input.filter { it.isDigit() }
                        val num = manualInputText.toIntOrNull()
                        if (num != null && num in 1..2000) {
                            onQuantityChanged(num)
                        }
                    },
                    label = { Text(stringResource(R.string.label_exact_quintals)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .testTag("manual_qty_field")
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("continue_step2_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
    ) {
        Text(
            text = stringResource(R.string.btn_continue),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// -------------------------------------------------------------------------
// STEP 3 — QUALITY (Section 7)
// -------------------------------------------------------------------------
@Composable
private fun StepQualityScreen(
    selectedCrop: CropOption,
    selectedQualityKey: String,
    onQualitySelected: (String, Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    StepHeaderWithProgress(
        stepNumber = 3,
        title = stringResource(R.string.step3_title),
        subtitle = stringResource(R.string.step3_subtitle),
        onBack = onBack
    )

    val options = listOf(
        Triple("good", R.string.quality_good, R.string.quality_good_desc),
        Triple("average", R.string.quality_average, R.string.quality_average_desc),
        Triple("poor", R.string.quality_poor, R.string.quality_poor_desc)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { (key, labelRes, descRes) ->
            val isSelected = selectedQualityKey == key
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onQualitySelected(key, labelRes) }
                    .testTag("quality_option_$key"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AgriGreenContainer else AgriSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AgriGreenPrimary else AgriGreenContainer)
                            .border(2.dp, AgriGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = when (key) {
                                    "good" -> "✓"
                                    "average" -> "~"
                                    else -> "!"
                                },
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(labelRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(descRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AgriGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Optional future-ready area (Section 7)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AgriCardBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = AgriTextMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.quality_photo_helper),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextMuted
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("continue_step3_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
    ) {
        Text(
            text = stringResource(R.string.btn_continue),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// -------------------------------------------------------------------------
// STEP 4 — LOCATION (Section 8)
// -------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepLocationScreen(
    selectedLocation: String,
    onLocationSelected: (String, Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var isManualChoice by remember { mutableStateOf(false) }

    StepHeaderWithProgress(
        stepNumber = 4,
        title = stringResource(R.string.step4_heading),
        subtitle = stringResource(R.string.step4_subtitle),
        onBack = onBack
    )

    val locations = listOf(
        R.string.loc_nagpur to "Nagpur, Maharashtra",
        R.string.loc_katol to "Katol, Maharashtra",
        R.string.loc_amravati to "Amravati, Maharashtra",
        R.string.loc_wardha to "Wardha, Maharashtra",
        R.string.loc_hingna to "Hingna, Maharashtra"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Choice 1: Use my location
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (!isManualChoice) 2.5.dp else 1.dp,
                    color = if (!isManualChoice) AgriGreenPrimary else AgriCardBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    isManualChoice = false
                    onLocationSelected("Nagpur, Maharashtra", R.string.loc_nagpur)
                }
                .testTag("location_use_my_loc"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (!isManualChoice) AgriGreenContainer else AgriSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (!isManualChoice) AgriGreenPrimary else AgriGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = if (!isManualChoice) Color.White else AgriGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.loc_use_my_location),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "Nagpur, Maharashtra",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriGreenLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Choice 2: Choose location
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isManualChoice) 2.5.dp else 1.dp,
                    color = if (isManualChoice) AgriGreenPrimary else AgriCardBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { isManualChoice = true }
                .testTag("location_choose_manual"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isManualChoice) AgriGreenContainer else AgriSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isManualChoice) AgriGreenPrimary else AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (isManualChoice) Color.White else AgriGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.loc_choose_location),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                if (isManualChoice) {
                    Spacer(modifier = Modifier.height(14.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        locations.forEach { (resId, name) ->
                            val isLocSelected = selectedLocation == name
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isLocSelected) AgriGreenPrimary else Color.White)
                                    .border(1.dp, if (isLocSelected) AgriGreenPrimary else AgriCardBorder, RoundedCornerShape(10.dp))
                                    .clickable { onLocationSelected(name, resId) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(resId),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isLocSelected) Color.White else AgriTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("continue_step4_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
    ) {
        Text(
            text = stringResource(R.string.btn_continue),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// -------------------------------------------------------------------------
// STEP 5 — HARVEST / READY DATE (Section 9)
// -------------------------------------------------------------------------
@Composable
private fun StepReadyDateScreen(
    selectedTiming: String,
    onTimingSelected: (String, Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    StepHeaderWithProgress(
        stepNumber = 5,
        title = stringResource(R.string.step5_title),
        subtitle = stringResource(R.string.step5_subtitle),
        onBack = onBack
    )

    val timingOptions = listOf(
        Triple("Ready now", R.string.timing_ready_now, R.string.timing_ready_now_desc),
        Triple("Ready in a few days", R.string.timing_few_days, R.string.timing_few_days_desc),
        Triple("Choose date", R.string.timing_choose_date, R.string.timing_choose_date_desc)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        timingOptions.forEach { (timingKey, titleRes, descRes) ->
            val isSelected = selectedTiming == timingKey
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onTimingSelected(timingKey, titleRes) }
                    .testTag("timing_option_$timingKey"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AgriGreenContainer else AgriSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AgriGreenPrimary else AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(imageVector = Icons.Default.HourglassEmpty, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(descRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("continue_step5_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
    ) {
        Text(
            text = stringResource(R.string.btn_continue),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// -------------------------------------------------------------------------
// STEP 6 — REVIEW BEFORE ANALYSIS (Section 10)
// -------------------------------------------------------------------------
@Composable
private fun StepReviewScreen(
    uiState: SellingUiState,
    onEditStep: (SellingStep) -> Unit,
    onFindBestOption: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.review_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.btn_back), tint = AgriGreenPrimary)
        }
    }
    Text(
        text = stringResource(R.string.review_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = AgriTextSecondary
    )

    Spacer(modifier = Modifier.height(20.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.review_header_produce),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = AgriGreenPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: Crop
            ReviewSummaryItem(
                icon = uiState.selectedCrop.emoji,
                label = stringResource(R.string.review_crop_label),
                value = stringResource(uiState.selectedCrop.nameRes),
                onEdit = { onEditStep(SellingStep.CROP) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AgriCardBorder.copy(alpha = 0.5f))

            // Row 2: Quantity
            ReviewSummaryItem(
                icon = "📦",
                label = stringResource(R.string.review_quantity_label),
                value = "${uiState.quantityQuintals} ${stringResource(R.string.unit_quintals)}",
                onEdit = { onEditStep(SellingStep.QUANTITY) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AgriCardBorder.copy(alpha = 0.5f))

            // Row 3: Quality
            ReviewSummaryItem(
                icon = "✓",
                label = stringResource(R.string.review_quality_label),
                value = stringResource(uiState.qualityRes),
                onEdit = { onEditStep(SellingStep.QUALITY) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AgriCardBorder.copy(alpha = 0.5f))

            // Row 4: Location
            ReviewSummaryItem(
                icon = "📍",
                label = stringResource(R.string.review_location_label),
                value = uiState.location,
                onEdit = { onEditStep(SellingStep.LOCATION) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AgriCardBorder.copy(alpha = 0.5f))

            // Row 5: Ready
            ReviewSummaryItem(
                icon = "⏱",
                label = stringResource(R.string.review_timing_label),
                value = stringResource(uiState.readyTimingRes),
                onEdit = { onEditStep(SellingStep.READY_DATE) }
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onFindBestOption,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("find_best_option_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
    ) {
        Text(
            text = stringResource(R.string.btn_find_best_option),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ReviewSummaryItem(
    icon: String,
    label: String,
    value: String,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = AgriTextMuted)
                Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
            }
        }
        TextButton(onClick = onEdit, modifier = Modifier.height(40.dp)) {
            Text(text = stringResource(R.string.review_edit), color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------------------
// STEP 7 — ANALYSIS / LOADING SCREEN (Section 11)
// -------------------------------------------------------------------------
@Composable
private fun StepAnalysisScreen(
    progressIndex: Int
) {
    val progressSteps = listOf(
        R.string.analysis_check_prices,
        R.string.analysis_compare_markets,
        R.string.analysis_check_demand,
        R.string.analysis_estimate_transport,
        R.string.analysis_compare_realization
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(54.dp),
            color = AgriGreenPrimary,
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.analysis_loading_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                progressSteps.forEachIndexed { index, stepResId ->
                    val isDone = index <= progressIndex
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, AgriCardBorder, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(stepResId),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                            color = if (isDone) AgriTextPrimary else AgriTextMuted
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// STEP 8 — RECOMMENDATION RESULTS & COMPARISON (Section 14-17, 25)
// -------------------------------------------------------------------------
@Composable
private fun StepRecommendationsScreen(
    uiState: SellingUiState,
    onSelectOpportunity: (SellingOpportunity) -> Unit,
    onEditDetails: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    if (uiState.recommendationError) {
        // Error State (Section 25)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = AgriGoldSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.error_rec_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(text = stringResource(R.string.btn_try_again), fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    if (uiState.recommendations.isEmpty()) {
        // Empty State (Section 25)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.empty_rec_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.empty_rec_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onEditDetails,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(text = stringResource(R.string.review_edit), fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // Results Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.rec_results_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AgriTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.rec_results_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )
        }
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(R.string.btn_back), tint = AgriGreenPrimary)
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    val topOption = uiState.recommendations.firstOrNull { it.isTopRecommendation } ?: uiState.recommendations.first()
    val otherOptions = uiState.recommendations.filter { it.id != topOption.id }

    // Primary Recommendation Card (⭐ BEST OPTION)
    TopRecommendationCard(
        opportunity = topOption,
        onSellHere = { onSelectOpportunity(topOption) }
    )

    // Other Good Options (Stacked Cards)
    if (otherOptions.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.badge_other_options),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            otherOptions.forEach { opp ->
                AlternativeOpportunityCard(
                    opportunity = opp,
                    onSellHere = { onSelectOpportunity(opp) }
                )
            }
        }
    }
}

@Composable
private fun TopRecommendationCard(
    opportunity: SellingOpportunity,
    onSellHere: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, AgriGreenPrimary, RoundedCornerShape(20.dp))
            .testTag("top_recommendation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Star Badge: ⭐ BEST OPTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriGreenPrimary)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.badge_top_pick),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Text(
                    text = stringResource(opportunity.statusTextRes),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenLight
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buyer Name & Verification
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(opportunity.buyerNameRes),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = stringResource(R.string.verified_buyer_tag),
                    tint = AgriGreenLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Key Financials
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.label_quoted_price_short),
                        style = MaterialTheme.typography.labelSmall,
                        color = AgriTextMuted
                    )
                    Text(
                        text = "₹${formatCurrency(opportunity.quotedPricePerQ)} / q",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.label_after_expenses_short),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                    Text(
                        text = "₹${formatCurrency(opportunity.netRealizationPerQ)} / q",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total In Pocket Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AgriGreenContainer)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_estimated_total_short),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriOnGreenContainer
                    )
                    Text(
                        text = "₹${formatCurrency(opportunity.estimatedTotalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button: Sell Here
            Button(
                onClick = onSellHere,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sell_here_top_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = stringResource(R.string.btn_sell_here),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable: "Why this option?"
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) stringResource(R.string.btn_hide_details) else stringResource(R.string.btn_why_this_option),
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = AgriGreenLight
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Explanations
                    opportunity.reasonsRes.forEach { reasonRes ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(reasonRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AgriTextPrimary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder.copy(alpha = 0.5f))

                    // Secondary Details (Section 16)
                    DetailRow(stringResource(R.string.detail_distance), "${opportunity.distanceKm} km")
                    DetailRow(stringResource(R.string.detail_transport), "₹${opportunity.transportExpensePerQ} / q")
                    DetailRow(stringResource(R.string.detail_handling), "₹${opportunity.otherExpensePerQ} / q")
                    DetailRow(stringResource(R.string.detail_payment), stringResource(opportunity.paymentReliabilityRes))
                }
            }
        }
    }
}

@Composable
private fun AlternativeOpportunityCard(
    opportunity: SellingOpportunity,
    onSellHere: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(opportunity.buyerNameRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AgriGoldContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(opportunity.statusTextRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriOnGoldContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${formatCurrency(opportunity.quotedPricePerQ)} / q",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextMuted
                    )
                    Text(
                        text = "₹${formatCurrency(opportunity.netRealizationPerQ)} / q ${stringResource(R.string.label_after_expenses_short).lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }

                Button(
                    onClick = onSellHere,
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(text = stringResource(R.string.btn_sell_here), fontWeight = FontWeight.Bold)
                }
            }

            // Why this option toggle
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) stringResource(R.string.btn_hide_details) else stringResource(R.string.btn_why_this_option),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AgriGreenLight
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = AgriGreenLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    opportunity.reasonsRes.forEach { reasonRes ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(reasonRes),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    DetailRow(stringResource(R.string.detail_distance), "${opportunity.distanceKm} km")
                    DetailRow(stringResource(R.string.detail_transport), "₹${opportunity.transportExpensePerQ} / q")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = AgriTextMuted)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AgriTextPrimary)
    }
}

// -------------------------------------------------------------------------
// STEP 9 — "SELL HERE" CONFIRMATION (Section 18)
// -------------------------------------------------------------------------
@Composable
private fun StepConfirmSellScreen(
    uiState: SellingUiState,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val opp = uiState.selectedOpportunity ?: return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.label_sell_to).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = AgriGreenPrimary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(opp.buyerNameRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${stringResource(uiState.selectedCrop.nameRes)} • ${uiState.quantityQuintals} ${stringResource(R.string.unit_quintals)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriGreenLight
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Summary Card highlighting Expected amount and transport
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriGreenLight.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.label_expected_amount_after_expenses),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AgriOnGreenContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "₹${formatCurrency(opp.estimatedTotalAmount)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = AgriGreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_estimated_transport),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹${formatCurrency(opp.transportExpensePerQ * uiState.quantityQuintals)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calculation Breakdown Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.breakdown_title),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = AgriGreenPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                val grossTotal = opp.quotedPricePerQ * uiState.quantityQuintals
                val transportTotal = opp.transportExpensePerQ * uiState.quantityQuintals
                val handlingTotal = opp.otherExpensePerQ * uiState.quantityQuintals

                BreakdownRow(
                    label = stringResource(R.string.breakdown_gross),
                    sub = "₹${formatCurrency(opp.quotedPricePerQ)} × ${uiState.quantityQuintals} q",
                    value = "₹${formatCurrency(grossTotal)}",
                    isNegative = false
                )

                BreakdownRow(
                    label = stringResource(R.string.breakdown_transport),
                    sub = "₹${opp.transportExpensePerQ} × ${uiState.quantityQuintals} q",
                    value = "- ₹${formatCurrency(transportTotal)}",
                    isNegative = true
                )

                BreakdownRow(
                    label = stringResource(R.string.breakdown_handling),
                    sub = "₹${opp.otherExpensePerQ} × ${uiState.quantityQuintals} q",
                    value = "- ₹${formatCurrency(handlingTotal)}",
                    isNegative = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.breakdown_net),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                    Text(
                        text = "₹${formatCurrency(opp.estimatedTotalAmount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.label_note_share_details),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = AgriTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("create_lot_confirm_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Text(
                text = stringResource(R.string.btn_create_lot),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_go_back"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_go_back),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AgriTextSecondary
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    sub: String,
    value: String,
    isNegative: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AgriTextPrimary)
            Text(text = sub, style = MaterialTheme.typography.labelSmall, color = AgriTextMuted)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isNegative) AgriGoldSecondary else AgriTextPrimary
        )
    }
}

// -------------------------------------------------------------------------
// STEP 10 — LOT CREATED (Section 19 & 20)
// -------------------------------------------------------------------------
@Composable
private fun StepLotCreatedScreen(
    uiState: SellingUiState,
    onViewMyLot: () -> Unit,
    onDone: () -> Unit
) {
    val lot = uiState.createdLot

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AgriGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AgriGreenPrimary,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.title_lot_created).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = AgriGreenPrimary,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.success_lot_created),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.success_lot_msg),
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lot?.lotId ?: "Lot #AG-1024",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgriGoldContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.lot_status_waiting),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGoldSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.selectedCrop.emoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(uiState.selectedCrop.nameRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "${uiState.quantityQuintals} ${stringResource(R.string.unit_quintals)} • ${stringResource(uiState.qualityRes)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        if (lot?.buyerNameRes != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${stringResource(R.string.label_buyer)}: ${stringResource(lot.buyerNameRes)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onViewMyLot,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("view_my_lot_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Text(
                text = stringResource(R.string.btn_view_my_lot),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("done_lot_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_done),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AgriTextSecondary
            )
        }
    }
}

// -------------------------------------------------------------------------
// Indian Currency Format Helper (e.g. 2,35,000)
// -------------------------------------------------------------------------
private fun formatCurrency(amount: Int): String {
    val s = amount.toString()
    if (s.length <= 3) return s
    val lastThree = s.takeLast(3)
    val remaining = s.dropLast(3)
    val formattedRemaining = remaining.reversed().chunked(2).joinToString(",").reversed()
    return "$formattedRemaining,$lastThree"
}
