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
// STEP 1 — CROP (Section 5)
// -------------------------------------------------------------------------
@Composable
fun StepCropScreen(
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

fun getCropVectorIcon(cropId: String): ImageVector {
    return when (cropId.lowercase()) {
        "soybean" -> Icons.Default.Spa
        "wheat" -> Icons.Default.Grain
        "rice" -> Icons.Default.Grass
        "maize" -> Icons.Default.Agriculture
        else -> Icons.Default.Category
    }
}

