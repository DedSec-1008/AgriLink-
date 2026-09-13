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
// STEP 9 — GUIDED LOT CREATION & PUBLISHING (Stage 7)
// -------------------------------------------------------------------------
@Composable
fun StepConfirmSellScreen(
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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

    val registryOwner = LocalActivityResultRegistryOwner.current
    val cameraPermissionLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
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
    } else null

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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            } else if (cameraPermissionLauncher != null) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            } else {
                                val photoUri = "content://media/photo_${System.currentTimeMillis()}"
                                onAddPhoto(photoUri)
                                showAddPhotoDialog = false
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isPublishing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
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

