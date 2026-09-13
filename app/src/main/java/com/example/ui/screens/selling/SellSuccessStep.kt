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
// STEP 10 — LOT PUBLISHED SUCCESS (Stage 7 Section 10 & 11)
// -------------------------------------------------------------------------
@Composable
fun StepLotCreatedScreen(
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
fun formatCurrency(amount: Int): String {
    val s = amount.toString()
    if (s.length <= 3) return s
    val lastThree = s.takeLast(3)
    val remaining = s.dropLast(3)
    val formattedRemaining = remaining.reversed().chunked(2).joinToString(",").reversed()
    return "$formattedRemaining,$lastThree"
}
