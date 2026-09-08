package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AgriRepository
import com.example.model.CropOption
import com.example.model.DestinationType
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
    modifier: Modifier = Modifier,
    onViewLotDetails: ((String) -> Unit)? = null,
    onViewOffers: ((String) -> Unit)? = null
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Render step by step
        when (uiState.currentStep) {
            SellingStep.CROP -> StepCropScreen(
                crops = repository.getAvailableCrops(),
                selectedCrop = uiState.selectedCrop,
                onCropSelected = { viewModel.setCrop(it) },
                onContinue = { viewModel.goToStep(SellingStep.QUANTITY) },
                onBack = onNavigateToHome
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
                quantity = uiState.quantityQuintals,
                selectedQualityKey = uiState.qualityKey,
                onQualitySelected = { key, resId ->
                    viewModel.selectQuality(key, resId)
                },
                onContinue = { viewModel.goToStep(SellingStep.LOCATION) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.LOCATION -> StepLocationScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                qualityRes = uiState.qualityRes,
                selectedLocation = uiState.location,
                locationSource = uiState.locationSource,
                onLocationSelected = { name, resId, source ->
                    viewModel.selectLocation(name, resId, source)
                },
                onContinue = { viewModel.goToStep(SellingStep.READY_DATE) },
                onBack = { viewModel.goBack() }
            )

            SellingStep.READY_DATE -> StepReadyDateScreen(
                selectedCrop = uiState.selectedCrop,
                quantity = uiState.quantityQuintals,
                qualityRes = uiState.qualityRes,
                selectedLocation = uiState.location,
                harvestReadiness = uiState.harvestReadiness,
                selectedTiming = uiState.readyTiming,
                onReadinessSelected = { readiness ->
                    viewModel.selectHarvestReadiness(readiness)
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
                viewModel = viewModel,
                onEditDetails = { viewModel.goToStep(SellingStep.REVIEW) },
                onConfirm = { viewModel.confirmCreateLot() },
                onBack = { viewModel.goBack() }
            )

            SellingStep.LOT_CREATED -> StepLotCreatedScreen(
                uiState = uiState,
                onViewMyLot = {
                    val lotId = uiState.createdLot?.lotId
                    if (lotId != null && onViewLotDetails != null) {
                        onViewLotDetails(lotId)
                    } else {
                        onNavigateToMyLots()
                    }
                },
                onViewOffers = {
                    val lotId = uiState.createdLot?.lotId
                    if (lotId != null && onViewOffers != null) {
                        onViewOffers(lotId)
                    } else {
                        onNavigateToMyLots()
                    }
                },
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
    onCropSelected: (CropOption) -> Unit,
    onContinue: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 2: Sell Screen Entry Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
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
            subtitle = stringResource(R.string.step1_subtitle),
            onBack = onBack
        )

        // Responsive Crop Cards List
        if (isLandscape) {
            // Landscape: 2-column grid to use width intelligently without stretching
            val rows = remember(crops) { crops.chunked(2) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { rowCrops ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCrops.forEach { crop ->
                            Box(modifier = Modifier.weight(1f)) {
                                CropSelectableCard(
                                    crop = crop,
                                    isSelected = selectedCrop.id == crop.id,
                                    onClick = { onCropSelected(crop) }
                                )
                            }
                        }
                        if (rowCrops.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // Portrait: Simple single-column layout with comfortable spacing
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                crops.forEach { crop ->
                    CropSelectableCard(
                        crop = crop,
                        isSelected = selectedCrop.id == crop.id,
                        onClick = { onCropSelected(crop) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Clear Primary Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("btn_crop_continue"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AgriGreenPrimary,
                contentColor = Color.White
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

@Composable
private fun CropSelectableCard(
    crop: CropOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .semantics {
                selected = isSelected
                role = Role.RadioButton
            }
            .testTag("crop_card_${crop.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AgriGreenContainer else AgriSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Vector Icon + Name & Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AgriGreenPrimary else AgriGreenContainer.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCropVectorIcon(crop.id),
                        contentDescription = stringResource(crop.nameRes),
                        tint = if (isSelected) Color.White else AgriGreenPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = stringResource(crop.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "~₹${crop.typicalPrice} / ${stringResource(R.string.unit_quintals)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextMuted
                    )
                }
            }

            // Right: Multi-modal selection indicator (border, shape, check icon, text badge - not color only!)
            if (isSelected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = AgriGreenPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.state_selected),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AgriGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.state_selected),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(2.dp, AgriCardBorder, CircleShape)
                )
            }
        }
    }
}

private fun getCropVectorIcon(cropId: String): ImageVector {
    return when (cropId.lowercase()) {
        "soybean" -> Icons.Default.Spa
        "wheat" -> Icons.Default.Grain
        "rice" -> Icons.Default.Grass
        "maize" -> Icons.Default.Agriculture
        else -> Icons.Default.Category
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

// -------------------------------------------------------------------------
// STEP 3 — QUALITY (Section 7)
// -------------------------------------------------------------------------
private data class QualityOptionItem(
    val key: String,
    val labelRes: Int,
    val descRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun StepQualityScreen(
    selectedCrop: CropOption,
    quantity: Int,
    selectedQualityKey: String,
    onQualitySelected: (String, Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var validationErrorVisible by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val options = remember {
        listOf(
            QualityOptionItem("good", R.string.quality_good, R.string.quality_good_desc, Icons.Default.Verified),
            QualityOptionItem("average", R.string.quality_average, R.string.quality_average_desc, Icons.Default.Spa),
            QualityOptionItem("poor", R.string.quality_poor, R.string.quality_poor_desc, Icons.Default.Info)
        )
    }

    if (isLandscape) {
        // Landscape 2-Column Responsive Layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Left Column: Step Header + Context Card + Validation + Continue + Camera Helper
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StepHeaderWithProgress(
                    stepNumber = 3,
                    title = stringResource(R.string.step3_title),
                    subtitle = stringResource(R.string.step3_subtitle),
                    onBack = onBack
                )

                QualityContextCard(selectedCrop = selectedCrop, quantity = quantity)

                AnimatedVisibility(
                    visible = validationErrorVisible && selectedQualityKey.isBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    QualityErrorBanner()
                }

                Button(
                    onClick = {
                        if (selectedQualityKey.isBlank()) {
                            validationErrorVisible = true
                        } else {
                            onContinue()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("continue_step3_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.btn_continue),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                QualityPhotoHelperCard()
            }

            // Right Column: Quality Cards
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                options.forEach { item ->
                    val isSelected = selectedQualityKey == item.key
                    QualityOptionCard(
                        item = item,
                        isSelected = isSelected,
                        onSelect = {
                            validationErrorVisible = false
                            onQualitySelected(item.key, item.labelRes)
                        }
                    )
                }
            }
        }
    } else {
        // Portrait Layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepHeaderWithProgress(
                stepNumber = 3,
                title = stringResource(R.string.step3_title),
                subtitle = stringResource(R.string.step3_subtitle),
                onBack = onBack
            )

            QualityContextCard(selectedCrop = selectedCrop, quantity = quantity)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { item ->
                    val isSelected = selectedQualityKey == item.key
                    QualityOptionCard(
                        item = item,
                        isSelected = isSelected,
                        onSelect = {
                            validationErrorVisible = false
                            onQualitySelected(item.key, item.labelRes)
                        }
                    )
                }
            }

            QualityPhotoHelperCard()

            AnimatedVisibility(
                visible = validationErrorVisible && selectedQualityKey.isBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QualityErrorBanner()
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (selectedQualityKey.isBlank()) {
                        validationErrorVisible = true
                    } else {
                        onContinue()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("continue_step3_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.btn_continue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QualityContextCard(
    selectedCrop: CropOption,
    quantity: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("step3_context_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer.copy(alpha = 0.55f)),
        border = BorderStroke(1.dp, AgriGreenPrimary.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AgriGreenPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCropVectorIcon(selectedCrop.id),
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(selectedCrop.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$quantity ${stringResource(R.string.unit_quintals)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AgriGreenLight
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AgriGreenPrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = stringResource(R.string.unit_quintals).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun QualityOptionCard(
    item: QualityOptionItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val selectedText = stringResource(R.string.state_selected)
    val label = stringResource(item.labelRes)
    val desc = stringResource(item.descRes)
    val semanticDescription = remember(isSelected, label, desc, selectedText) {
        if (isSelected) "$label, $desc, $selectedText" else "$label, $desc"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .border(
                border = BorderStroke(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) AgriGreenPrimary else AgriCardBorder
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .semantics {
                this.role = Role.RadioButton
                this.selected = isSelected
                this.contentDescription = semanticDescription
            }
            .testTag("quality_option_${item.key}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AgriGreenContainer else AgriSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AgriGreenPrimary else AgriGreenContainer.copy(alpha = 0.55f))
                    .border(
                        BorderStroke(
                            width = if (isSelected) 0.dp else 1.5.dp,
                            color = if (isSelected) Color.Transparent else AgriGreenPrimary.copy(alpha = 0.5f)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else AgriGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AgriGreenPrimary
                    ) {
                        Text(
                            text = selectedText,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(AgriGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(BorderStroke(2.dp, AgriCardBorder), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun QualityErrorBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("error_quality_required"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.err_quality_required),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun QualityPhotoHelperCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, AgriCardBorder.copy(alpha = 0.5f)), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = AgriTextMuted,
                modifier = Modifier.size(22.dp)
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

// -------------------------------------------------------------------------
// STEP 4 — LOCATION (Section 8)
// -------------------------------------------------------------------------
@Composable
private fun StepLocationScreen(
    selectedCrop: CropOption,
    quantity: Int,
    qualityRes: Int,
    selectedLocation: String,
    locationSource: LocationSource,
    onLocationSelected: (String, Int, LocationSource) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isDetecting by rememberSaveable { mutableStateOf(false) }
    var locationError by rememberSaveable { mutableStateOf<String?>(null) }
    var isManualExpanded by rememberSaveable {
        mutableStateOf(locationSource == LocationSource.MANUAL_SELECTION || selectedLocation.isEmpty())
    }

    val demoLocations = listOf(
        R.string.loc_nagpur to "Nagpur, Maharashtra",
        R.string.loc_amravati to "Amravati, Maharashtra",
        R.string.loc_katol to "Katol, Maharashtra",
        R.string.loc_wardha to "Wardha, Maharashtra",
        R.string.loc_hingna to "Hingna, Maharashtra"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            isDetecting = true
            locationError = null
            detectDeviceLocation(context) { name, resId, error ->
                isDetecting = false
                if (error != null) {
                    locationError = error
                } else if (name != null && resId != null) {
                    onLocationSelected(name, resId, LocationSource.CURRENT_LOCATION)
                    isManualExpanded = false
                }
            }
        } else {
            isDetecting = false
            locationError = "PERMISSION_DENIED"
        }
    }

    val handleCurrentLocationClick = {
        locationError = null
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            isDetecting = true
            detectDeviceLocation(context) { name, resId, error ->
                isDetecting = false
                if (error != null) {
                    locationError = error
                } else if (name != null && resId != null) {
                    onLocationSelected(name, resId, LocationSource.CURRENT_LOCATION)
                    isManualExpanded = false
                }
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val isContinueEnabled = selectedLocation.isNotBlank()

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StepHeaderWithProgress(
                    stepNumber = 4,
                    title = stringResource(R.string.step4_heading),
                    subtitle = stringResource(R.string.step4_subtitle),
                    onBack = onBack
                )

                ProduceContextSummaryCard(
                    selectedCrop = selectedCrop,
                    quantity = quantity,
                    qualityRes = qualityRes
                )

                LocationPrivacyNoteCard()
            }

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CurrentLocationActionCard(
                    isSelected = locationSource == LocationSource.CURRENT_LOCATION && selectedLocation.isNotBlank(),
                    isDetecting = isDetecting,
                    selectedLocation = selectedLocation,
                    onClick = handleCurrentLocationClick
                )

                LocationErrorBanner(
                    locationError = locationError,
                    onRetry = handleCurrentLocationClick,
                    onChooseManually = {
                        locationError = null
                        isManualExpanded = true
                    }
                )

                ManualLocationSection(
                    isExpanded = isManualExpanded,
                    onToggleExpanded = { isManualExpanded = !isManualExpanded },
                    demoLocations = demoLocations,
                    selectedLocation = selectedLocation,
                    onSelectLocation = { name, resId ->
                        onLocationSelected(name, resId, LocationSource.MANUAL_SELECTION)
                        locationError = null
                    }
                )

                if (selectedLocation.isNotBlank()) {
                    ConfirmedLocationCard(
                        selectedLocation = selectedLocation,
                        locationSource = locationSource,
                        onChangeLocation = { isManualExpanded = true }
                    )
                }

                LocationContinueSection(
                    isContinueEnabled = isContinueEnabled,
                    onContinue = onContinue
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .widthIn(max = 680.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StepHeaderWithProgress(
                stepNumber = 4,
                title = stringResource(R.string.step4_heading),
                subtitle = stringResource(R.string.step4_subtitle),
                onBack = onBack
            )

            ProduceContextSummaryCard(
                selectedCrop = selectedCrop,
                quantity = quantity,
                qualityRes = qualityRes
            )

            LocationPrivacyNoteCard()

            CurrentLocationActionCard(
                isSelected = locationSource == LocationSource.CURRENT_LOCATION && selectedLocation.isNotBlank(),
                isDetecting = isDetecting,
                selectedLocation = selectedLocation,
                onClick = handleCurrentLocationClick
            )

            LocationErrorBanner(
                locationError = locationError,
                onRetry = handleCurrentLocationClick,
                onChooseManually = {
                    locationError = null
                    isManualExpanded = true
                }
            )

            ManualLocationSection(
                isExpanded = isManualExpanded,
                onToggleExpanded = { isManualExpanded = !isManualExpanded },
                demoLocations = demoLocations,
                selectedLocation = selectedLocation,
                onSelectLocation = { name, resId ->
                    onLocationSelected(name, resId, LocationSource.MANUAL_SELECTION)
                    locationError = null
                }
            )

            if (selectedLocation.isNotBlank()) {
                ConfirmedLocationCard(
                    selectedLocation = selectedLocation,
                    locationSource = locationSource,
                    onChangeLocation = { isManualExpanded = true }
                )
            }

            LocationContinueSection(
                isContinueEnabled = isContinueEnabled,
                onContinue = onContinue
            )
        }
    }
}

@Composable
private fun ProduceContextSummaryCard(
    selectedCrop: CropOption,
    quantity: Int,
    qualityRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("step4_produce_context"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, AgriHeroGreenBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = selectedCrop.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = stringResource(selectedCrop.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "$quantity quintals",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AgriGreenPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, AgriGreenLight)
            ) {
                Text(
                    text = stringResource(qualityRes),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
            }
        }
    }
}

@Composable
private fun LocationPrivacyNoteCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("location_privacy_note"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AgriCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AgriGreenLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.loc_privacy_note),
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun CurrentLocationActionCard(
    isSelected: Boolean,
    isDetecting: Boolean,
    selectedLocation: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag("location_use_my_loc"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AgriGreenContainer.copy(alpha = 0.45f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AgriGreenPrimary else AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                if (isDetecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = if (isSelected) Color.White else AgriGreenPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = stringResource(R.string.loc_use_my_location),
                        tint = if (isSelected) Color.White else AgriGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.loc_use_my_location),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Text(
                    text = when {
                        isDetecting -> stringResource(R.string.loc_detecting)
                        isSelected && selectedLocation.isNotBlank() -> "${stringResource(R.string.loc_detected_badge)} • $selectedLocation"
                        else -> "Tap to detect farm location"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) AgriGreenLight else AgriTextSecondary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.state_selected),
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun LocationErrorBanner(
    locationError: String?,
    onRetry: () -> Unit,
    onChooseManually: () -> Unit
) {
    AnimatedVisibility(visible = locationError != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("location_error_banner"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5)),
            border = BorderStroke(1.dp, Color(0xFFFFB74D))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    val errorMsg = when (locationError) {
                        "PERMISSION_DENIED" -> stringResource(R.string.loc_permission_needed)
                        "SERVICES_DISABLED" -> stringResource(R.string.loc_services_disabled)
                        else -> stringResource(R.string.loc_unavailable)
                    }
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (locationError == "PERMISSION_DENIED" || locationError == "SERVICES_DISABLED") {
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("location_try_again_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.btn_try_again), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    OutlinedButton(
                        onClick = onChooseManually,
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("location_error_choose_manual_button")
                    ) {
                        Text(stringResource(R.string.btn_choose_manually), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualLocationSection(
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    demoLocations: List<Pair<Int, String>>,
    selectedLocation: String,
    onSelectLocation: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AgriCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("location_choose_manual"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpanded),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AgriGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.loc_choose_location),
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.loc_choose_location),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.select_location_prompt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                }

                IconButton(
                    onClick = onToggleExpanded,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = AgriTextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = AgriCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    demoLocations.forEach { (resId, name) ->
                        val isLocSelected = selectedLocation == name
                        val town = name.substringBefore(',').trim()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .semantics {
                                    role = Role.RadioButton
                                    selected = isLocSelected
                                }
                                .border(
                                    width = if (isLocSelected) 2.dp else 1.dp,
                                    color = if (isLocSelected) AgriGreenPrimary else AgriCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectLocation(name, resId) }
                                .testTag("location_option_$town"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLocSelected) AgriGreenContainer.copy(alpha = 0.6f) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isLocSelected) AgriGreenPrimary else AgriTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(resId),
                                        fontWeight = if (isLocSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 15.sp,
                                        color = if (isLocSelected) AgriGreenPrimary else AgriTextPrimary
                                    )
                                }
                                if (isLocSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = stringResource(R.string.state_selected),
                                        tint = AgriGreenPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .border(1.5.dp, AgriCardBorder, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmedLocationCard(
    selectedLocation: String,
    locationSource: LocationSource,
    onChangeLocation: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, AgriGreenPrimary, RoundedCornerShape(16.dp))
            .testTag("location_confirmed_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.loc_your_location),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (locationSource == LocationSource.CURRENT_LOCATION) AgriGreenPrimary else AgriHeroGreenBorder
                ) {
                    Text(
                        text = stringResource(
                            if (locationSource == LocationSource.CURRENT_LOCATION)
                                R.string.loc_detected_badge
                            else
                                R.string.loc_manual_badge
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (locationSource == LocationSource.CURRENT_LOCATION) Color.White else AgriGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = selectedLocation,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = onChangeLocation,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("location_change_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AgriGreenPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.loc_change_manual),
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
            }
        }
    }
}

@Composable
private fun LocationContinueSection(
    isContinueEnabled: Boolean,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onContinue,
            enabled = isContinueEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("continue_step4_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AgriGreenPrimary,
                disabledContainerColor = AgriGreenPrimary.copy(alpha = 0.38f)
            )
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

        if (!isContinueEnabled) {
            Text(
                text = stringResource(R.string.err_location_required),
                color = AgriTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}

private fun detectDeviceLocation(
    context: Context,
    onResult: (name: String?, resId: Int?, error: String?) -> Unit
) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onResult(null, null, "UNAVAILABLE")
            return
        }

        val isGpsEnabled = try { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (e: Exception) { false }
        val isNetworkEnabled = try { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (e: Exception) { false }

        if (!isGpsEnabled && !isNetworkEnabled) {
            onResult(null, null, "SERVICES_DISABLED")
            return
        }

        var lastLocation: android.location.Location? = null
        try {
            if (isGpsEnabled) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (lastLocation == null && isNetworkEnabled) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: SecurityException) {
            onResult(null, null, "PERMISSION_DENIED")
            return
        }

        var resolvedName = "Nagpur, Maharashtra"
        val resolvedRes = R.string.loc_nagpur

        if (lastLocation != null) {
            try {
                if (android.location.Geocoder.isPresent()) {
                    val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                        val state = addr.adminArea ?: "Maharashtra"
                        if (!city.isNullOrBlank()) {
                            resolvedName = "$city, $state"
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to default
            }
        }

        onResult(resolvedName, resolvedRes, null)
    } catch (e: Exception) {
        onResult(null, null, "UNAVAILABLE")
    }
}

// -------------------------------------------------------------------------
// STEP 5 — HARVEST / READY DATE (Section 9)
// -------------------------------------------------------------------------
@Composable
private fun StepReadyDateScreen(
    selectedCrop: CropOption,
    quantity: Int,
    qualityRes: Int,
    selectedLocation: String,
    harvestReadiness: HarvestReadiness,
    selectedTiming: String,
    onReadinessSelected: (HarvestReadiness) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenWidthDp = configuration.screenWidthDp
    val canContinue = harvestReadiness != HarvestReadiness.NONE

    // Use two-column layout in landscape when width is sufficient (>= 560dp)
    val useTwoColumn = isLandscape && screenWidthDp >= 560

    if (useTwoColumn) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 960.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Left Column: Step header, produce summary context, and why timing matters
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StepHeaderWithProgress(
                    stepNumber = 5,
                    totalSteps = 5,
                    title = stringResource(R.string.step5_heading),
                    subtitle = stringResource(R.string.step5_subtitle),
                    onBack = onBack
                )

                Step5ProduceContextCard(
                    selectedCrop = selectedCrop,
                    quantity = quantity,
                    qualityRes = qualityRes,
                    selectedLocation = selectedLocation
                )

                TimingExplanationCard()
            }

            // Right Column: Options & Action Button
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReadinessOptionsList(
                    harvestReadiness = harvestReadiness,
                    onReadinessSelected = onReadinessSelected
                )

                Spacer(modifier = Modifier.height(4.dp))

                Step5ContinueSection(
                    isEnabled = canContinue,
                    onContinue = onContinue
                )
            }
        }
    } else {
        // Portrait Layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StepHeaderWithProgress(
                stepNumber = 5,
                totalSteps = 5,
                title = stringResource(R.string.step5_heading),
                subtitle = stringResource(R.string.step5_subtitle),
                onBack = onBack
            )

            Step5ProduceContextCard(
                selectedCrop = selectedCrop,
                quantity = quantity,
                qualityRes = qualityRes,
                selectedLocation = selectedLocation
            )

            TimingExplanationCard()

            ReadinessOptionsList(
                harvestReadiness = harvestReadiness,
                onReadinessSelected = onReadinessSelected
            )

            Spacer(modifier = Modifier.height(10.dp))

            Step5ContinueSection(
                isEnabled = canContinue,
                onContinue = onContinue
            )
        }
    }
}

@Composable
private fun Step5ProduceContextCard(
    selectedCrop: CropOption,
    quantity: Int,
    qualityRes: Int,
    selectedLocation: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("step5_produce_context"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, AgriHeroGreenBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.summary_produce_label),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(text = selectedCrop.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(selectedCrop.nameRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "$quantity ${stringResource(R.string.unit_quintals)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AgriGreenPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, AgriGreenLight)
                ) {
                    Text(
                        text = stringResource(if (qualityRes != 0) qualityRes else R.string.produce_quality_good),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }
            }

            HorizontalDivider(color = AgriHeroGreenBorder.copy(alpha = 0.4f), thickness = 1.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = selectedLocation.ifBlank { stringResource(R.string.loc_nagpur) },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AgriTextPrimary
                )
            }
        }
    }
}

@Composable
private fun TimingExplanationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timing_why_matters_note"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AgriCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.timing_why_matters),
                style = MaterialTheme.typography.bodyMedium,
                color = AgriTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ReadinessOptionsList(
    harvestReadiness: HarvestReadiness,
    onReadinessSelected: (HarvestReadiness) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        HarvestReadinessCard(
            readiness = HarvestReadiness.READY_NOW,
            titleRes = R.string.timing_ready_now,
            subtitleRes = R.string.timing_ready_now_sub,
            icon = Icons.Default.CheckCircle,
            isSelected = harvestReadiness == HarvestReadiness.READY_NOW,
            onSelect = { onReadinessSelected(HarvestReadiness.READY_NOW) },
            testTag = "timing_option_ready_now"
        )

        HarvestReadinessCard(
            readiness = HarvestReadiness.WITHIN_7_DAYS,
            titleRes = R.string.timing_within_7_days,
            subtitleRes = R.string.timing_within_7_days_sub,
            icon = Icons.Default.DateRange,
            isSelected = harvestReadiness == HarvestReadiness.WITHIN_7_DAYS,
            onSelect = { onReadinessSelected(HarvestReadiness.WITHIN_7_DAYS) },
            testTag = "timing_option_within_7_days"
        )

        HarvestReadinessCard(
            readiness = HarvestReadiness.LATER,
            titleRes = R.string.timing_later,
            subtitleRes = R.string.timing_later_sub,
            icon = Icons.Default.HourglassEmpty,
            isSelected = harvestReadiness == HarvestReadiness.LATER,
            onSelect = { onReadinessSelected(HarvestReadiness.LATER) },
            testTag = "timing_option_later"
        )
    }
}

@Composable
private fun HarvestReadinessCard(
    readiness: HarvestReadiness,
    titleRes: Int,
    subtitleRes: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
            .semantics {
                role = Role.RadioButton
                selected = isSelected
            }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AgriGreenContainer.copy(alpha = 0.45f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AgriGreenPrimary else AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else AgriGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) AgriGreenPrimary else AgriTextPrimary
                    )
                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AgriGreenPrimary
                        ) {
                            Text(
                                text = stringResource(R.string.state_selected),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(subtitleRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AgriGreenPrimary else Color.Transparent)
                    .border(
                        width = if (isSelected) 0.dp else 2.dp,
                        color = if (isSelected) Color.Transparent else AgriCardBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.state_selected),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step5ContinueSection(
    isEnabled: Boolean,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onContinue,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
                .testTag("continue_step5_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AgriGreenPrimary,
                disabledContainerColor = AgriGreenPrimary.copy(alpha = 0.38f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.btn_see_best_options),
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

        if (!isEnabled) {
            Text(
                text = stringResource(R.string.err_timing_required),
                color = AgriTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
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
                value = if (uiState.readyTimingRes != 0) stringResource(uiState.readyTimingRes) else uiState.readyTiming,
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
            .testTag("analysis_loading_container")
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

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.analysis_loading_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
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
// STEP 6 — INTELLIGENT SELLING RECOMMENDATION
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(18.dp))
                .testTag("rec_error_container"),
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
                    text = stringResource(R.string.error_no_selling_options),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.error_rec_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("rec_go_back_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = stringResource(R.string.btn_back), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("rec_try_again_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Text(text = stringResource(R.string.btn_try_again), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    if (uiState.recommendations.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(18.dp))
                .testTag("rec_empty_container"),
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
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("change_details_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(text = stringResource(R.string.btn_change_details), fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val topOption = uiState.bestRecommendation
        ?: uiState.recommendations.firstOrNull { it.isTopRecommendation }
        ?: uiState.recommendations.first()
    val otherOptions = if (uiState.alternativeRecommendations.isNotEmpty()) {
        uiState.alternativeRecommendations
    } else {
        uiState.recommendations.filter { it.id != topOption.id }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp)
    ) {
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
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("rec_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = AgriGreenPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Compact Input Summary Card (Section 1)
        ProduceSummaryCard(
            uiState = uiState,
            onEditDetails = onEditDetails
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Comparison Explanation Banner (Section 8)
        ComparisonExplanationBanner()

        Spacer(modifier = Modifier.height(16.dp))

        // Responsive Body: Single column for portrait, two columns for wide/landscape (Section 16)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWideLayout = maxWidth >= 680.dp

            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1.1f)) {
                        TopRecommendationCard(
                            opportunity = topOption,
                            onSellHere = { onSelectOpportunity(topOption) }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.9f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (otherOptions.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.badge_other_options),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary,
                                modifier = Modifier.testTag("label_other_good_options")
                            )
                            otherOptions.forEach { opp ->
                                AlternativeOpportunityCard(
                                    opportunity = opp,
                                    onSellHere = { onSelectOpportunity(opp) }
                                )
                            }
                        }

                        // Secondary Change Details Action
                        OutlinedButton(
                            onClick = onEditDetails,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("change_details_button_secondary"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.btn_change_details),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TopRecommendationCard(
                        opportunity = topOption,
                        onSellHere = { onSelectOpportunity(topOption) }
                    )

                    if (otherOptions.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.badge_other_options),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary,
                            modifier = Modifier.testTag("label_other_good_options")
                        )

                        otherOptions.forEach { opp ->
                            AlternativeOpportunityCard(
                                opportunity = opp,
                                onSellHere = { onSelectOpportunity(opp) }
                            )
                        }
                    }

                    // Secondary Change Details Action
                    OutlinedButton(
                        onClick = onEditDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("change_details_button_secondary"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_change_details),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProduceSummaryCard(
    uiState: SellingUiState,
    onEditDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AgriCardBorder, RoundedCornerShape(14.dp))
            .testTag("step6_produce_summary"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = uiState.selectedCrop.emoji,
                    fontSize = 24.sp
                )
                Column {
                    val cropName = stringResource(uiState.selectedCrop.nameRes)
                    val qualityName = if (uiState.qualityRes != 0) {
                        stringResource(uiState.qualityRes)
                    } else {
                        stringResource(R.string.produce_quality_good)
                    }
                    val locName = if (uiState.locationRes != 0) {
                        stringResource(uiState.locationRes)
                    } else {
                        uiState.location.ifEmpty { "Nagpur, Maharashtra" }
                    }
                    val timingName = if (uiState.readyTimingRes != 0) {
                        stringResource(uiState.readyTimingRes)
                    } else {
                        uiState.readyTiming.ifEmpty { stringResource(R.string.timing_ready_now) }
                    }

                    Text(
                        text = "$cropName • ${uiState.quantityQuintals} q",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "$qualityName • $locName • $timingName",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }

            TextButton(
                onClick = onEditDetails,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("change_details_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.btn_change_details),
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.btn_change_details),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
            }
        }
    }
}

@Composable
private fun ComparisonExplanationBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rec_comparison_explanation_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer.copy(alpha = 0.55f)),
        border = BorderStroke(1.dp, AgriGreenLight.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AgriGreenPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.rec_comparison_explanation),
                style = MaterialTheme.typography.bodySmall,
                color = AgriOnGreenContainer,
                lineHeight = 18.sp
            )
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
            .testTag("top_recommendation_card")
            .testTag("best_recommendation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: BEST PLACE TO SELL & Match quality badge
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
                        .testTag("best_rec_header")
                ) {
                    Text(
                        text = stringResource(R.string.header_best_place_to_sell),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("best_rec_strength_badge")
                ) {
                    Text(
                        text = stringResource(opportunity.matchQualityRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriOnGreenContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Destination Name & Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = stringResource(opportunity.buyerNameRes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary,
                        modifier = Modifier.testTag("best_rec_destination_name")
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Destination Type Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (opportunity.isMarket) AgriGoldContainer else AgriGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("best_rec_destination_type")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (opportunity.isMarket) Icons.Default.Storefront else Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (opportunity.isMarket) AgriGoldSecondary else AgriGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (opportunity.isMarket) {
                                stringResource(R.string.tag_market_apmc)
                            } else {
                                stringResource(R.string.tag_verified_buyer)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (opportunity.isMarket) AgriOnGoldContainer else AgriOnGreenContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Produce Info & Distance
            Text(
                text = "${stringResource(opportunity.cropNameRes)} • ${opportunity.quantityQuintals} ${stringResource(R.string.unit_quintals)} • ${opportunity.distanceKm} km",
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Key Financials: Quoted, Deductions, and In Pocket
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, AgriCardBorder.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                color = AgriTextSecondary,
                                modifier = Modifier.testTag("best_rec_quoted_price")
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = stringResource(R.string.label_estimated_expenses_deduction),
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextMuted
                            )
                            Text(
                                text = "- ₹${opportunity.totalExpensePerQ} / q",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AgriTextSecondary,
                                modifier = Modifier.testTag("best_rec_deductions")
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = AgriCardBorder.copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.label_estimated_in_pocket),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                            Text(
                                text = stringResource(R.string.label_after_expenses_short),
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextMuted
                            )
                        }

                        Text(
                            text = "₹${formatCurrency(opportunity.netRealizationPerQ)} / q",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = AgriGreenPrimary,
                            modifier = Modifier.testTag("best_rec_net_price")
                        )
                    }
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
                    Column {
                        Text(
                            text = stringResource(R.string.label_for_quintals_total, opportunity.quantityQuintals),
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriOnGreenContainer
                        )
                        Text(
                            text = stringResource(R.string.label_estimated_total_short),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriOnGreenContainer
                        )
                    }
                    Text(
                        text = "₹${formatCurrency(opportunity.estimatedTotalAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary,
                        modifier = Modifier.testTag("best_rec_total_amount")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Disclaimer text
            Text(
                text = stringResource(R.string.estimate_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Why this is recommended - Reason items with checkmarks (Section 6)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("best_rec_reasons_list")
            ) {
                Text(
                    text = stringResource(R.string.header_why_recommended),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                opportunity.reasonsRes.take(4).forEach { reasonRes ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(reasonRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextPrimary
                        )
                    }
                }
            }

            // Buyer Trust Info (Section 11)
            if (!opportunity.isMarket) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buyer_trust_info"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriSurface),
                    border = BorderStroke(1.dp, AgriCardBorder.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = AgriGreenLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.buyer_payment_timeline_days, opportunity.paymentDaysEstimate),
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                        }

                        Text(
                            text = stringResource(R.string.buyer_completed_tx_count, opportunity.completedTransactionsCount),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button: Sell Here (Primary Action)
            Button(
                onClick = onSellHere,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sell_here_best_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = stringResource(R.string.btn_sell_here),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable: "Why this suggestion?"
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("why_this_suggestion_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) {
                            stringResource(R.string.btn_hide_details)
                        } else {
                            stringResource(R.string.btn_why_this_suggestion)
                        },
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
                        .testTag("why_suggestion_explanation_card")
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AgriSurface),
                        border = BorderStroke(1.dp, AgriCardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.why_suggestion_engine_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriTextPrimary
                            )
                            WhyFactorRow(
                                title = stringResource(R.string.why_factor_price_title),
                                desc = stringResource(R.string.why_factor_price_desc)
                            )
                            WhyFactorRow(
                                title = stringResource(R.string.why_factor_travel_title),
                                desc = stringResource(R.string.why_factor_travel_desc, opportunity.distanceKm)
                            )
                            WhyFactorRow(
                                title = stringResource(R.string.why_factor_demand_title),
                                desc = stringResource(R.string.why_factor_demand_desc)
                            )
                            WhyFactorRow(
                                title = stringResource(R.string.why_factor_quality_title),
                                desc = stringResource(R.string.why_factor_quality_desc)
                            )
                            WhyFactorRow(
                                title = stringResource(R.string.why_factor_quantity_title),
                                desc = stringResource(R.string.why_factor_quantity_desc, opportunity.quantityQuintals)
                            )
                            WhyFactorRow(
                                title = stringResource(R.string.why_factor_reliability_title),
                                desc = stringResource(R.string.why_factor_reliability_desc)
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = AgriCardBorder.copy(alpha = 0.5f)
                            )

                            DetailRow(stringResource(R.string.detail_distance), "${opportunity.distanceKm} km")
                            DetailRow(stringResource(R.string.detail_transport), "₹${opportunity.transportExpensePerQ} / q")
                            DetailRow(stringResource(R.string.detail_handling), "₹${opportunity.otherExpensePerQ} / q")
                            DetailRow(stringResource(R.string.detail_payment), stringResource(opportunity.paymentReliabilityRes))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WhyFactorRow(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = AgriGreenPrimary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary,
                fontSize = 12.sp
            )
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
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(18.dp))
            .testTag("alternative_card_${opportunity.id}"),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(opportunity.buyerNameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "${opportunity.distanceKm} km • ${stringResource(opportunity.cropNameRes)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (opportunity.isMarket) AgriGoldContainer else AgriGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (opportunity.isMarket) {
                            stringResource(R.string.tag_market_apmc)
                        } else {
                            stringResource(R.string.tag_verified_buyer)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (opportunity.isMarket) AgriOnGoldContainer else AgriOnGreenContainer
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
                    Text(
                        text = "₹${formatCurrency(opportunity.estimatedTotalAmount)} (${opportunity.quantityQuintals} q)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextSecondary
                    )
                }

                Button(
                    onClick = onSellHere,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("sell_here_alt_${opportunity.id}"),
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
                    .height(48.dp)
                    .padding(top = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) {
                            stringResource(R.string.btn_hide_details)
                        } else {
                            stringResource(R.string.btn_why_this_option)
                        },
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
                    DetailRow(stringResource(R.string.detail_handling), "₹${opportunity.otherExpensePerQ} / q")
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
// STEP 9 — GUIDED LOT CREATION & PUBLISHING (Stage 7)
// -------------------------------------------------------------------------
@Composable
private fun StepConfirmSellScreen(
    uiState: SellingUiState,
    viewModel: SellingViewModel,
    onEditDetails: () -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val opp = uiState.selectedOpportunity ?: return
    val currentStep = uiState.lotCreationStep

    Column(modifier = Modifier.fillMaxWidth()) {
        // Progress Header: e.g. "Step 1 of 4 • Produce Summary"
        LotProgressHeader(
            stepNumber = currentStep.stepNumber,
            totalSteps = 4,
            title = stringResource(currentStep.stepTitleRes)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (currentStep) {
            LotCreationStep.PRODUCE_SUMMARY -> {
                LotStep1ProduceSummary(
                    uiState = uiState,
                    opp = opp,
                    onContinue = { viewModel.nextLotCreationStep() },
                    onEditDetails = onEditDetails,
                    onBack = onBack
                )
            }
            LotCreationStep.CHECK_DETAILS -> {
                LotStep2CheckDetails(
                    uiState = uiState,
                    opp = opp,
                    onContinue = { viewModel.nextLotCreationStep() },
                    onEditDetails = onEditDetails,
                    onBack = { viewModel.previousLotCreationStep() }
                )
            }
            LotCreationStep.ADD_PHOTOS -> {
                LotStep3AddPhotos(
                    uiState = uiState,
                    onAddPhoto = { uri -> viewModel.addPhoto(uri) },
                    onRemovePhoto = { uri -> viewModel.removePhoto(uri) },
                    onContinue = { viewModel.nextLotCreationStep() },
                    onSkip = { viewModel.nextLotCreationStep() },
                    onBack = { viewModel.previousLotCreationStep() }
                )
            }
            LotCreationStep.READY_TO_PUBLISH -> {
                LotStep4ReadyToPublish(
                    uiState = uiState,
                    opp = opp,
                    isPublishing = uiState.isPublishingLot,
                    error = uiState.lotPublishError,
                    onPublish = { viewModel.publishLot() },
                    onRetry = { viewModel.publishLot() },
                    onBack = { viewModel.previousLotCreationStep() }
                )
            }
        }
    }
}

@Composable
private fun LotProgressHeader(
    stepNumber: Int,
    totalSteps: Int,
    title: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.step_progress_indicator, stepNumber, totalSteps),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { stepNumber / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = AgriGreenPrimary,
            trackColor = AgriGreenContainer
        )
    }
}

// -------------------------------------------------------------------------
// LOT STEP 1 — PRODUCE SUMMARY
// -------------------------------------------------------------------------
@Composable
private fun LotStep1ProduceSummary(
    uiState: SellingUiState,
    opp: SellingOpportunity,
    onContinue: () -> Unit,
    onEditDetails: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.title_step_produce_summary),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Review your crop details and selected buyer before creating your lot.",
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card 1: Produce Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.header_your_produce),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    TextButton(
                        onClick = onEditDetails,
                        modifier = Modifier.testTag("lot_btn_change_details")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.btn_change_details),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.selectedCrop.emoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(uiState.selectedCrop.nameRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "${uiState.quantityQuintals} ${stringResource(R.string.unit_quintals)} • ${stringResource(uiState.qualityRes)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = AgriCardBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📍 ${uiState.location.ifBlank { "Nagpur, Maharashtra" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = uiState.readinessTiming,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriGreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: Recommended / Selected Buyer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriGreenLight.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.header_recommended_buyer),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = AgriOnGreenContainer,
                        letterSpacing = 0.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.badge_verified_buyer),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(opp.buyerNameRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.label_quoted_price),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹${formatCurrency(opp.quotedPricePerQ)} / q",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.label_net_expected),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹${formatCurrency(opp.estimatedNetPricePerQ)} / q",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgriGreenPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_lot_step1_continue"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Text(
                text = stringResource(R.string.btn_continue),
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
                .testTag("btn_lot_step1_back"),
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

// -------------------------------------------------------------------------
// LOT STEP 2 — CHECK DETAILS
// -------------------------------------------------------------------------
@Composable
private fun LotStep2CheckDetails(
    uiState: SellingUiState,
    opp: SellingOpportunity,
    onContinue: () -> Unit,
    onEditDetails: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.title_step_check_details),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Verify your location, readiness and expected net return.",
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Location row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.label_pickup_location),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.location.ifBlank { "Nagpur, Maharashtra" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }
                    TextButton(onClick = onEditDetails) {
                        Text(
                            text = stringResource(R.string.btn_change_details),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder.copy(alpha = 0.5f))

                // Readiness row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.label_harvest_readiness),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.readinessTiming,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder.copy(alpha = 0.5f))

                // Logistics / Pickup mode
                Column {
                    Text(
                        text = "Transport & Handling",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Direct pickup from farm by ${stringResource(opp.buyerNameRes)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "Estimated transport expense: ₹${formatCurrency(opp.transportExpensePerQ * uiState.quantityQuintals)} (deducted at settlement)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder.copy(alpha = 0.5f))

                // Net expected total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.label_expected_amount_after_expenses),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "Net estimated return",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                    Text(
                        text = "₹${formatCurrency(opp.estimatedTotalAmount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Estimate Disclaimer Note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AgriGoldContainer.copy(alpha = 0.5f))
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AgriGoldSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.disclaimer_estimate_text),
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_lot_step2_continue"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Text(
                text = stringResource(R.string.btn_continue),
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
                .testTag("btn_lot_step2_back"),
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

// -------------------------------------------------------------------------
// LOT STEP 3 — OPTIONAL PHOTOS
// -------------------------------------------------------------------------
@Composable
private fun LotStep3AddPhotos(
    uiState: SellingUiState,
    onAddPhoto: (String) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }
    var cameraPermissionDenied by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraPermissionDenied = false
            val photoUri = "content://media/photo_${System.currentTimeMillis()}"
            onAddPhoto(photoUri)
            showAddPhotoDialog = false
        } else {
            cameraPermissionDenied = true
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.title_add_photos),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.subtitle_add_photos),
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (uiState.lotPhotos.isEmpty()) {
                    // Empty photo placeholder
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AgriGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.no_photos_added),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a clear photo of your grain or storage bag",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddPhotoDialog = true },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("btn_add_photo"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.btn_add_photo),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Photos list
                    Text(
                        text = stringResource(R.string.photos_attached_count, uiState.lotPhotos.size),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.lotPhotos.forEachIndexed { index, uri ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AgriSurface)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AgriGreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = uiState.selectedCrop.emoji, fontSize = 24.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.photo_thumbnail, index + 1),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AgriTextPrimary
                                        )
                                        Text(
                                            text = "Attached for buyers",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AgriTextSecondary
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = { previewPhotoUri = uri },
                                        modifier = Modifier.testTag("btn_view_photo_$index")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = stringResource(R.string.btn_view_photo),
                                            tint = AgriGreenPrimary
                                        )
                                    }
                                    IconButton(
                                        onClick = { onRemovePhoto(uri) },
                                        modifier = Modifier.testTag("btn_remove_photo_$index")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.btn_remove_photo),
                                            tint = Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { showAddPhotoDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_add_another_photo"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_add_another_photo),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }
            }
        }

        if (cameraPermissionDenied) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.camera_permission_explanation),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFDC2626)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main action: Continue if photos exist, Skip if empty
        if (uiState.lotPhotos.isNotEmpty()) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_continue_photos"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = stringResource(R.string.btn_continue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Button(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_skip_photos"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = stringResource(R.string.btn_skip_photos),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_lot_step3_back"),
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

    // Add Photo Selection Dialog
    if (showAddPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showAddPhotoDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.title_add_photos),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val hasCamera = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasCamera) {
                                val photoUri = "content://media/photo_${System.currentTimeMillis()}"
                                onAddPhoto(photoUri)
                                showAddPhotoDialog = false
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_btn_take_photo"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.btn_take_photo), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val photoUri = "content://gallery/photo_${System.currentTimeMillis()}"
                            onAddPhoto(photoUri)
                            showAddPhotoDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_btn_gallery"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_choose_gallery),
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val sampleUri = "android.resource://sample_harvest_${System.currentTimeMillis()}"
                            onAddPhoto(sampleUri)
                            showAddPhotoDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dialog_btn_sample_photo"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_add_sample_photo),
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextSecondary
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddPhotoDialog = false }) {
                    Text(text = "Cancel", color = AgriTextSecondary)
                }
            }
        )
    }

    // Photo Preview Dialog
    if (previewPhotoUri != null) {
        AlertDialog(
            onDismissRequest = { previewPhotoUri = null },
            title = {
                Text(
                    text = "Photo Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.selectedCrop.emoji, fontSize = 64.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.sample_photo_attached),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { previewPhotoUri = null },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(text = "Close")
                }
            }
        )
    }
}

// -------------------------------------------------------------------------
// LOT STEP 4 — READY TO PUBLISH
// -------------------------------------------------------------------------
@Composable
private fun LotStep4ReadyToPublish(
    uiState: SellingUiState,
    opp: SellingOpportunity,
    isPublishing: Boolean,
    error: String?,
    onPublish: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.title_ready_to_sell),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.desc_publishing_explanation),
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Final Recap Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Crop & Quantity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.selectedCrop.emoji, fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(uiState.selectedCrop.nameRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = "${uiState.quantityQuintals} ${stringResource(R.string.unit_quintals)} • ${stringResource(uiState.qualityRes)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = AgriTextSecondary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder.copy(alpha = 0.5f))

                // Location & Readiness
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Location",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = uiState.location.ifBlank { "Nagpur, Maharashtra" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Target Buyer",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = stringResource(opp.buyerNameRes),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AgriCardBorder.copy(alpha = 0.5f))

                // Expected Net Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.label_estimated_after_expenses_short),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹${formatCurrency(opp.quotedPricePerQ)} / q quoted",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                    Text(
                        text = "₹${formatCurrency(opp.estimatedTotalAmount)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )
                }

                if (uiState.lotPhotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "📷 ${stringResource(R.string.photos_attached_count, uiState.lotPhotos.size)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Error message if publishing failed
        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFEE2E2))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFDC2626)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF991B1B)
                    )
                }
                TextButton(onClick = onRetry) {
                    Text("Retry", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Publish Button with loading indicator
        Button(
            onClick = onPublish,
            enabled = !isPublishing,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_publish_lot")
                .testTag("create_lot_confirm_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            if (isPublishing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.status_publishing_lot),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = stringResource(R.string.btn_publish_lot),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            enabled = !isPublishing,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_publish_go_back")
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

// -------------------------------------------------------------------------
// STEP 10 — LOT PUBLISHED SUCCESS (Stage 7 Section 10 & 11)
// -------------------------------------------------------------------------
@Composable
private fun StepLotCreatedScreen(
    uiState: SellingUiState,
    onViewMyLot: () -> Unit,
    onViewOffers: () -> Unit,
    onDone: () -> Unit
) {
    val lot = uiState.createdLot

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
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

        Spacer(modifier = Modifier.height(14.dp))

        // Status badge: ✓ Lot published
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AgriGreenContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("badge_lot_published")
        ) {
            Text(
                text = stringResource(R.string.badge_lot_published),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = AgriGreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.success_lot_published_msg,
                stringResource(uiState.selectedCrop.nameRes)
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Published Lot Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriCardBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lot?.lotId ?: "LOT-AGL-2026-...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary,
                        modifier = Modifier.testTag("published_lot_id")
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AgriGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(lot?.statusRes ?: R.string.lot_status_published),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
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
                        if (lot?.location?.isNotBlank() == true) {
                            Text(
                                text = "📍 ${lot.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AgriTextSecondary
                            )
                        }
                    }
                }

                if (lot?.buyerNameRes != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = AgriCardBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.label_recommended_buyer_tag),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = stringResource(lot.buyerNameRes),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_expected_price_short),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹${formatCurrency(lot?.expectedPricePerQ ?: 4850)}/q",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_estimated_after_expenses_short),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹${formatCurrency(lot?.estimatedTotalAmount ?: 235000)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Action: View My Lot
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

        Spacer(modifier = Modifier.height(10.dp))

        // Tertiary / Secondary: See Offers
        OutlinedButton(
            onClick = onViewOffers,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("see_offers_lot_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_see_offers),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Done / Go Home
        TextButton(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("done_lot_button")
                .testTag("btn_go_home")
        ) {
            Text(
                text = stringResource(R.string.btn_go_home),
                style = MaterialTheme.typography.bodyMedium,
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
