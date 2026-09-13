package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AgriRepository
import com.example.model.Buyer
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenText
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BuyerProfileScreen(
    buyerId: String,
    repository: AgriRepository,
    onBack: () -> Unit,
    onSellToBuyer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buyer = repository.getBuyerById(buyerId) ?: return

    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = numberFormat.format(buyer.quotedPricePerQ)
    var showMoreInfo by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("buyer_profile_screen")
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onBack() }
                    .testTag("btn_back_buyer_profile"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = AgriTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(buyer.nameRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AgriTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(buyer.locationRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }
        }

        // Verification Badge Card
        if (buyer.isVerified) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.buyer_verified),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenText
                        )
                        Text(
                            text = stringResource(R.string.buyer_verified_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }
            }
        }

        // Section 1: Current Demand
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.section_current_demand),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(buyer.commodityRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "${buyer.currentDemandMinQ}–${buyer.currentDemandMaxQ} ${stringResource(R.string.unit_quintals)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(buyer.requiredQualityRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = stringResource(R.string.label_needed_timeline, stringResource(buyer.neededTimelineRes)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextSecondary
                    )
                }
            }
        }

        // Section 2: Quoted Price & Payout
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.section_price),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹$formattedPrice / ${stringResource(R.string.unit_quintals_short)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.buyer_quoted_directly_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )
            }
        }

        // Section 3: Trust & Performance Metrics
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Payment Speed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.section_payment),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(buyer.paymentDaysDescriptionRes),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = stringResource(R.string.buyer_stat_ontime, buyer.onTimePaymentPct),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriGreenText
                        )
                    }
                }

                // Completed Transactions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.section_transactions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = stringResource(R.string.buyer_stat_completed_tx, buyer.completedTransactions),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                // Farmer Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_farmer_rating),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f / 5".format(Locale.US, buyer.farmerRating),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }
                }
            }
        }

        // Section 4: Progressive Disclosure ("More about this buyer")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showMoreInfo = !showMoreInfo },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_more_about_buyer),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Icon(
                        imageVector = if (showMoreInfo) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = AgriTextSecondary
                    )
                }

                AnimatedVisibility(visible = showMoreInfo) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "• ${stringResource(R.string.buyer_stat_dispute_rate, buyer.disputeRatePct)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "• ${stringResource(R.string.buyer_stat_avg_payment, buyer.averagePaymentDays)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "• ${stringResource(R.string.label_payment_terms_colon, stringResource(buyer.paymentTermsRes))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "• ${stringResource(R.string.label_delivery_colon, stringResource(buyer.deliveryTermsRes))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "• ${stringResource(R.string.label_quality_standards_colon, stringResource(buyer.qualityRequirementsRes))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Primary & Secondary Actions
        Button(
            onClick = onSellToBuyer,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_sell_to_this_buyer"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Text(
                text = stringResource(R.string.btn_sell_to_buyer),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_view_demand"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_view_demand),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
