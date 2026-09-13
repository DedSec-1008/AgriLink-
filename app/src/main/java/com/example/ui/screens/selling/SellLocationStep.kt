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
// STEP 4 — LOCATION (Section 8)
// -------------------------------------------------------------------------
@Composable
fun StepLocationScreen(
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
    val coroutineScope = rememberCoroutineScope()

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

    val registryOwner = LocalActivityResultRegistryOwner.current
    val permissionLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fineGranted || coarseGranted) {
                isDetecting = true
                locationError = null
                detectDeviceLocation(context, coroutineScope) { name, resId, error ->
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
    } else null

    val handleCurrentLocationClick = {
        locationError = null
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            isDetecting = true
            detectDeviceLocation(context, coroutineScope) { name, resId, error ->
                isDetecting = false
                if (error != null) {
                    locationError = error
                } else if (name != null && resId != null) {
                    onLocationSelected(name, resId, LocationSource.CURRENT_LOCATION)
                    isManualExpanded = false
                }
            }
        } else if (permissionLauncher != null) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            isDetecting = true
            detectDeviceLocation(context, coroutineScope) { name, resId, error ->
                isDetecting = false
                if (error != null) {
                    locationError = error
                } else if (name != null && resId != null) {
                    onLocationSelected(name, resId, LocationSource.CURRENT_LOCATION)
                    isManualExpanded = false
                }
            }
        }
    }

    val isContinueEnabled = selectedLocation.isNotBlank()

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
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
                modifier = Modifier.weight(1.2f),
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
                .fillMaxWidth()
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                containerColor = if (isLocSelected) AgriGreenContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
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
    coroutineScope: CoroutineScope,
    onResult: (name: String?, resId: Int?, error: String?) -> Unit
) {
    coroutineScope.launch {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager == null) {
                onResult(null, null, "UNAVAILABLE")
                return@launch
            }

            val isGpsEnabled = try { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (e: Exception) { false }
            val isNetworkEnabled = try { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (e: Exception) { false }

            if (!isGpsEnabled && !isNetworkEnabled) {
                onResult(null, null, "SERVICES_DISABLED")
                return@launch
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
                return@launch
            }

            var resolvedName = "Nagpur, Maharashtra"
            val resolvedRes = R.string.loc_nagpur

            if (lastLocation != null) {
                val geocodedName = withContext(Dispatchers.IO) {
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
                                    "$city, $state"
                                } else null
                            } else null
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
                if (geocodedName != null) {
                    resolvedName = geocodedName
                }
            }

            onResult(resolvedName, resolvedRes, null)
        } catch (e: Exception) {
            onResult(null, null, "UNAVAILABLE")
        }
    }
}

