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
// STEP 6 — REVIEW BEFORE ANALYSIS (Section 10)
// -------------------------------------------------------------------------
@Composable
fun StepReviewScreen(
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
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back), tint = AgriGreenPrimary)
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

