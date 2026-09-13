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
// STEP 5 — HARVEST / READY DATE (Section 9)
// -------------------------------------------------------------------------
@Composable
fun StepReadyDateScreen(
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
            containerColor = if (isSelected) AgriGreenContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
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

