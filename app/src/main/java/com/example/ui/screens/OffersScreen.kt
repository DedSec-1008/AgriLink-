package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.Offer
import com.example.model.OfferStatus
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenText
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OffersScreen(
    lotId: String,
    repository: AgriRepository,
    onBack: () -> Unit,
    onViewOfferDetails: (String) -> Unit,
    onAcceptOfferClicked: (String) -> Unit,
    onFindBuyers: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val offers by repository.getOffersForLot(lotId).collectAsState(initial = emptyList())
    val lot = repository.getLotById(lotId)

    val bestOffer = offers.find { it.isBestOffer } ?: offers.maxByOrNull { it.estimatedNetAmount }
    val otherOffers = offers.filter { it.id != bestOffer?.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("offers_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
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
                        .testTag("btn_back_offers"),
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
                        text = if (lot != null) {
                            "${stringResource(R.string.title_offers_for_produce)} (${stringResource(lot.cropNameRes)})"
                        } else {
                            stringResource(R.string.title_offers_for_produce)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.subtitle_offers),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }
        }

        if (offers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .testTag("empty_offers_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(AgriGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.empty_offers_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )

                        Text(
                            text = stringResource(R.string.empty_offers_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                repository.generateOffersForLot(lotId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_check_offers"),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.btn_check_offers),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (onFindBuyers != null) {
                            OutlinedButton(
                                onClick = { onFindBuyers(lotId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_find_buyers"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.btn_find_buyers),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // 1. BEST OFFER HERO CARD
            if (bestOffer != null) {
                item {
                    BestOfferCard(
                        offer = bestOffer,
                        onViewDetails = { onViewOfferDetails(bestOffer.id) },
                        onAccept = { onAcceptOfferClicked(bestOffer.id) }
                    )
                }
            }

            // 2. NET REALIZATION COMPARISON SECTION
            item {
                NetRealizationComparisonCard(
                    offers = offers
                )
            }

            // 3. OTHER OFFERS
            if (otherOffers.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.label_other_offers),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(otherOffers, key = { it.id }) { offer ->
                    OfferCard(
                        offer = offer,
                        onViewDetails = { onViewOfferDetails(offer.id) },
                        onAccept = { onAcceptOfferClicked(offer.id) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BestOfferCard(
    offer: Offer,
    onViewDetails: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedQuotedRate = numberFormat.format(offer.pricePerQuintal)
    val formattedNetTotal = numberFormat.format(offer.estimatedNetAmount)
    val formattedNetRate = numberFormat.format(offer.estimatedNetPricePerQ)
    val isAccepted = offer.status == OfferStatus.ACCEPTED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("best_offer_card_${offer.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0FDF4)
        ),
        border = BorderStroke(1.5.dp, AgriHeroGreenBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Best Offer Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriGreenPrimary)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⭐ ${stringResource(R.string.badge_best_offer)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.badge_best_for_you),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buyer info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(offer.buyerNameRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AgriTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${offer.distanceKm} km • ${stringResource(offer.reliabilityTextRes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                if (offer.isVerifiedBuyer) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenContainer)
                            .border(1.dp, AgriHeroGreenBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.badge_verified_buyer_check),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expected in pocket callout (Net Realization)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                border = BorderStroke(1.dp, AgriHeroGreenBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_after_expenses),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenText
                        )
                        Text(
                            text = "₹$formattedNetTotal",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgriGreenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${stringResource(R.string.label_quoted_price)}: ₹$formattedQuotedRate / q",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹$formattedNetRate / q net",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Why this offer is better checklist
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.why_this_offer_better),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenText
                )
                val reasons = if (offer.whyBetterReasons.isNotEmpty()) {
                    offer.whyBetterReasons
                } else {
                    listOf(
                        R.string.reason_higher_net,
                        R.string.reason_verified_buyer_check,
                        R.string.reason_reliable_payment_check,
                        R.string.reason_suitable_quantity_check
                    )
                }
                reasons.forEach { reasonRes ->
                    Text(
                        text = stringResource(reasonRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Minimum 48dp touch targets)
            if (isAccepted) {
                Button(
                    onClick = onViewDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_details_offer_${offer.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.btn_view_details),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_details_offer_${offer.id}"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_view_details),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("btn_accept_offer_${offer.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.btn_accept_offer),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetRealizationComparisonCard(
    offers: List<Offer>,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("net_realization_comparison_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.compare_net_realization_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
            }

            Text(
                text = stringResource(R.string.compare_net_realization_desc),
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            HorizontalDivider(color = Color(0xFFF3F4F6))

            // Compare rows for each offer
            offers.forEach { offer ->
                val isBest = offer.isBestOffer
                val quotedRate = numberFormat.format(offer.pricePerQuintal)
                val transportCost = numberFormat.format(offer.transportExpensePerQ)
                val netRate = numberFormat.format(offer.estimatedNetPricePerQ)
                val netTotal = numberFormat.format(offer.estimatedNetAmount)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isBest) Color(0xFFF0FDF4) else Color(0xFFF9FAFB))
                        .border(
                            1.dp,
                            if (isBest) AgriHeroGreenBorder else Color(0xFFE5E7EB),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(offer.buyerNameRes),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        if (isBest) {
                            Text(
                                text = "⭐ ${stringResource(R.string.badge_best_for_you)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rate: ₹$quotedRate  •  Transport: -₹$transportCost",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹$netRate/q net (₹$netTotal)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isBest) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isBest) AgriGreenPrimary else AgriTextPrimary
                        )
                    }
                }
            }

            // Key takeaway banner explaining why ABC Foods gives more pocket earnings
            val abcOffer = offers.find { it.buyerId == "buyer_abc" }
            val xyzOffer = offers.find { it.buyerId == "buyer_xyz" }
            if (abcOffer != null && xyzOffer != null) {
                val diff = abcOffer.estimatedNetAmount - xyzOffer.estimatedNetAmount
                val formattedDiff = numberFormat.format(diff)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 ${stringResource(abcOffer.buyerNameRes)} gives you ₹$formattedDiff more in your pocket despite the lower quoted rate, due to significantly lower transport deductions.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF92400E)
                    )
                }
            }
        }
    }
}

@Composable
fun OfferCard(
    offer: Offer,
    onViewDetails: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = numberFormat.format(offer.pricePerQuintal)
    val formattedNet = numberFormat.format(offer.estimatedNetAmount)
    val formattedTransport = numberFormat.format(offer.transportExpensePerQ)
    val formattedNetRate = numberFormat.format(offer.estimatedNetPricePerQ)

    val isAccepted = offer.status == OfferStatus.ACCEPTED
    val isCancelled = offer.status == OfferStatus.CANCELLED
    val isRejected = offer.status == OfferStatus.REJECTED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offer_card_${offer.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccepted) AgriGreenContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Buyer Name & Status / Verified
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(offer.buyerNameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "%.1f".format(Locale.US, offer.farmerRating),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Text(
                            text = " • ${offer.distanceKm} km • ${stringResource(offer.reliabilityTextRes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                    }
                }

                if (isAccepted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.offer_status_accepted),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenText
                        )
                    }
                } else if (offer.isVerifiedBuyer) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriGreenContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.badge_verified_buyer_check),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing details & deductions breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.label_quoted_price),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹$formattedPrice / quintal",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "Transport: -₹$formattedTransport / q",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.label_after_expenses),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriGreenText
                    )
                    Text(
                        text = "₹$formattedNet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                    Text(
                        text = "₹$formattedNetRate / q net",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Speed
            Text(
                text = "${stringResource(R.string.section_payment)}: ${stringResource(offer.paymentTermsRes)}",
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Minimum 48dp touch targets)
            if (isAccepted) {
                Button(
                    onClick = onViewDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_details_offer_${offer.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.btn_view_details),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else if (isCancelled) {
                Text(
                    text = stringResource(R.string.offer_status_cancelled),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextMuted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else if (isRejected) {
                Text(
                    text = stringResource(R.string.offer_status_rejected),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextMuted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_details_offer_${offer.id}"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_view_details),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("btn_accept_offer_${offer.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.btn_accept_offer),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
