package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AgriRepository
import com.example.model.ProduceLot
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGoldContainer
import com.example.ui.theme.AgriGoldSecondary
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import kotlinx.coroutines.launch

@Composable
fun MyLotsScreen(
    repository: AgriRepository,
    onNavigateToSell: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onViewLotDetails: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lots by repository.getMyLots().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.title_my_lots),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.subtitle_my_lots),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                }

                Button(
                    onClick = onNavigateToSell,
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.btn_new_lot), fontWeight = FontWeight.Bold)
                }
            }
        }

        if (lots.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AgriGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.empty_lots_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.empty_lots_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onNavigateToSell,
                            modifier = Modifier.height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Text(text = stringResource(R.string.btn_create_first_lot), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(lots, key = { it.lotId }) { lot ->
                ProduceLotCard(
                    lot = lot,
                    onViewLot = {
                        if (onViewLotDetails != null) {
                            onViewLotDetails(lot.lotId)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${lot.lotId}: Offers from ABC Foods and 2 other verified buyers are being matched."
                                )
                            }
                        }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProduceLotCard(
    lot: ProduceLot,
    onViewLot: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp))
            .testTag("lot_card_${lot.lotId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Lot ID and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lot.lotId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenPrimary
                )

                val isPublished = lot.statusRes == R.string.lot_status_published || lot.lotStatus == com.example.model.LotStatus.PUBLISHED
                val isAccepted = lot.statusRes in listOf(
                    R.string.status_offer_accepted,
                    R.string.tx_status_offer_accepted,
                    R.string.tx_status_logistics_booked,
                    R.string.tx_status_dispatched,
                    R.string.tx_status_delivered,
                    R.string.tx_status_payment_initiated,
                    R.string.tx_status_payment_delayed,
                    R.string.tx_status_completed
                )
                val isGreenStatus = isAccepted || isPublished
                Box(
                    modifier = Modifier
                        .testTag("lot_status_tag_${lot.lotId}")
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGreenStatus) AgriGreenContainer else AgriGoldContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isPublished -> Icons.Default.Inventory2
                                isAccepted -> Icons.Default.Inventory2
                                else -> Icons.Default.HourglassEmpty
                            },
                            contentDescription = null,
                            tint = if (isGreenStatus) AgriGreenPrimary else AgriGoldSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(lot.statusRes),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isGreenStatus) AgriGreenPrimary else AgriGoldSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crop & Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AgriGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = lot.iconEmoji, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(lot.cropNameRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "${lot.quantityQuintals} ${stringResource(R.string.unit_quintals)} • ${stringResource(lot.qualityRes)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AgriTextSecondary
                    )
                    if (lot.location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📍 ${lot.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary,
                            modifier = Modifier.testTag("lot_location_${lot.lotId}")
                        )
                    }
                    if (lot.buyerNameRes != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${stringResource(R.string.label_recommended_buyer_tag)}: ${stringResource(lot.buyerNameRes)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onViewLot,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("btn_view_lot"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriGreenPrimary)
                ) {
                    Text(
                        text = stringResource(R.string.btn_view_lot),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
