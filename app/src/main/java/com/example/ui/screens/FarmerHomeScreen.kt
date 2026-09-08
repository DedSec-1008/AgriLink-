package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AgriRepository
import com.example.model.AgriTransaction
import com.example.ui.components.OpportunityCard
import com.example.ui.components.QuickActionGrid
import com.example.ui.components.TodaysPriceCard
import com.example.ui.components.WhatIHaveCard
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenText
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

@Composable
fun FarmerHomeScreen(
    repository: AgriRepository,
    onNavigateToSell: () -> Unit,
    onNavigateToPrices: () -> Unit,
    onNavigateToMyLots: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToBuyers: () -> Unit = {},
    onNavigateToActiveSale: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentProduce = repository.getCurrentProduce()
    val todaysPrice = repository.getTodaysPrimaryPrice()
    val bestOpportunity = repository.getBestSellingOpportunity()
    val activeTransactions by repository.getActiveTransactions().collectAsState(initial = emptyList())
    val activeSale = activeTransactions.firstOrNull()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }

    val inventorySection = @Composable {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (activeSale != null) {
                ActiveSaleCard(
                    activeSale = activeSale,
                    onNavigateToActiveSale = onNavigateToActiveSale
                )
            }
            WhatIHaveCard(produce = currentProduce)
            TodaysPriceCard(priceInfo = todaysPrice)
            if (isLandscape) {
                BrowseBuyersCard(onNavigateToBuyers = onNavigateToBuyers)
            }
        }
    }

    val actionSection = @Composable {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            WhereShouldISellHeader()
            OpportunityCard(
                opportunity = bestOpportunity,
                onSellHereClicked = onNavigateToSell,
                onFindBestPlaceClicked = onNavigateToSell
            )
            QuickActionGrid(
                onCheckPricesClicked = onNavigateToPrices,
                onSellProduceClicked = onNavigateToSell,
                onMyLotsClicked = onNavigateToMyLots,
                onGetHelpClicked = onNavigateToHelp
            )
            if (!isLandscape) {
                BrowseBuyersCard(onNavigateToBuyers = onNavigateToBuyers)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (isLandscape) 1100.dp else 640.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = if (isLandscape) 10.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 16.dp)
        ) {
            FarmerGreetingHeader()

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        inventorySection()
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        actionSection()
                    }
                }
            } else {
                inventorySection()
                actionSection()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FarmerGreetingHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.greeting_farmer),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AgriTextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AgriGreenContainer)
                    .border(1.dp, AgriHeroGreenBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.location_nagpur),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_greeting_subtitle),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = AgriTextSecondary
        )
    }
}

@Composable
private fun WhereShouldISellHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
        border = BorderStroke(1.dp, AgriHeroGreenBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AgriGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.home_hierarchy_step3),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenPrimary,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = stringResource(R.string.home_primary_question),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )
            }
        }
    }
}

@Composable
private fun ActiveSaleCard(
    activeSale: AgriTransaction,
    onNavigateToActiveSale: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriHeroGreenBorder, RoundedCornerShape(16.dp))
            .testTag("card_active_sale"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = stringResource(R.string.home_active_sale_title),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenText,
                    letterSpacing = 0.8.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AgriGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(activeSale.status.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(activeSale.buyerNameRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )

            Text(
                text = "${stringResource(activeSale.cropNameRes)} · ${activeSale.quantityQuintals} quintals",
                style = MaterialTheme.typography.bodyMedium,
                color = AgriTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val nextActionLabel = when (activeSale.status) {
                com.example.model.TransactionStatus.OFFER_ACCEPTED -> stringResource(R.string.next_step_arrange_transport)
                com.example.model.TransactionStatus.LOGISTICS_BOOKED -> stringResource(R.string.action_confirm_pickup)
                com.example.model.TransactionStatus.DISPATCHED -> stringResource(R.string.action_confirm_delivery)
                com.example.model.TransactionStatus.DELIVERED, com.example.model.TransactionStatus.PAYMENT_INITIATED -> stringResource(R.string.action_confirm_payment)
                com.example.model.TransactionStatus.PAYMENT_DELAYED -> stringResource(R.string.payment_delayed_header)
                com.example.model.TransactionStatus.PAYMENT_RECEIVED, com.example.model.TransactionStatus.COMPLETED -> stringResource(R.string.title_sale_completed)
                else -> stringResource(R.string.btn_view_sale)
            }

            Text(
                text = "Next: $nextActionLabel",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AgriGreenPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onNavigateToActiveSale(activeSale.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("btn_view_active_sale_from_home"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(
                    text = stringResource(R.string.btn_view_sale),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun BrowseBuyersCard(
    onNavigateToBuyers: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToBuyers() }
            .testTag("card_browse_buyers"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AgriGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.title_buyers),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = stringResource(R.string.home_browse_buyers_sub),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = AgriTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
