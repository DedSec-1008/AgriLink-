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
// STEP 6 — INTELLIGENT SELLING RECOMMENDATION
// -------------------------------------------------------------------------
@Composable
fun StepRecommendationsScreen(
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AgriTextPrimary,
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
                                style = MaterialTheme.typography.titleSmall,
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
                            style = MaterialTheme.typography.headlineSmall,
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
                        style = MaterialTheme.typography.headlineSmall,
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

