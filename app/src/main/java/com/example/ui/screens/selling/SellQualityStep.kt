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
// STEP 3 — QUALITY (Section 7)
// -------------------------------------------------------------------------
private data class QualityOptionItem(
    val key: String,
    val labelRes: Int,
    val descRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun StepQualityScreen(
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

