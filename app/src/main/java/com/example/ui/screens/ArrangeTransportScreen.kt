package com.example.ui.screens

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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.model.TransporterOption
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenText
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ArrangeTransportScreen(
    transactionId: String,
    repository: AgriRepository,
    onBack: () -> Unit,
    onTransportArranged: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transaction = repository.getTransactionById(transactionId)
    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val scope = rememberCoroutineScope()

    val cropRes = transaction?.cropNameRes ?: R.string.crop_soybean
    val quantity = transaction?.quantityQuintals ?: 50
    val buyerName = if (transaction != null) stringResource(transaction.buyerNameRes) else "ABC Foods"
    val agreedPrice = transaction?.agreedPricePerQ ?: 4850

    val transporterOptions = remember { repository.getTransporterOptions(cropRes, quantity) }
    var selectedOption by remember { mutableStateOf(transporterOptions.firstOrNull()) }

    var isBooked by remember { mutableStateOf(transaction?.transporterBooking != null) }
    var isSimulatingUnavailable by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val currentTransporter = selectedOption ?: transporterOptions.firstOrNull()
    val estCost = currentTransporter?.totalCost ?: 6000
    val estCostPerQ = currentTransporter?.costPerQuintal ?: 120
    val otherCostsPerQ = 30
    val pocketPerQ = agreedPrice - estCostPerQ - otherCostsPerQ
    val totalPocket = pocketPerQ * quantity

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("arrange_transport_screen")
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
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
                    .testTag("btn_back_arrange_transport"),
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
                    text = stringResource(R.string.title_arrange_transport),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextPrimary
                )
                Text(
                    text = stringResource(R.string.subtitle_arrange_transport),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )
            }
        }

        if (isBooked) {
            // Transport Arranged Success State
            val booking = transaction?.transporterBooking
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transport_arranged_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.transport_arranged_header),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${booking?.transporterName ?: currentTransporter?.name ?: "Truck"} · ${stringResource(booking?.deliveryTimingRes ?: R.string.delivery_today)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pickup: Your location  ➔  Delivery: $buyerName",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estimated cost: ₹${numberFormat.format(booking?.totalCost ?: estCost)} (deducted from final sale)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onTransportArranged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_view_sale_after_booking"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.btn_view_sale),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        } else if (isSimulatingUnavailable || transporterOptions.isEmpty()) {
            // Unavailable Transport Empty State
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_transport_unavailable"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.no_transport_available),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.transport_failure_msg),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isSimulatingUnavailable = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_try_again"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Text(text = stringResource(R.string.btn_try_again), fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { isSimulatingUnavailable = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_choose_another"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = stringResource(R.string.btn_choose_another), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Logistics Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_logistics_details"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.label_pickup_location),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "Your location",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.label_delivery_location),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = buyerName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.label_distance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "28 km",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Produce",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "${stringResource(cropRes)} — $quantity quintals",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated transport cost",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹${numberFormat.format(estCost)} (Estimated)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated transport cost per quintal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹$estCostPerQ / quintal (Estimated)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated delivery",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = stringResource(currentTransporter?.deliveryTimingRes ?: R.string.delivery_today),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                    }
                }
            }

            // Net Realization Breakdown (Cost Transparency)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_net_realization_breakdown"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAF9)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NET REALIZATION BREAKDOWN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary,
                        letterSpacing = 1.sp
                    )

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Buyer price",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹${numberFormat.format(agreedPrice)}/q",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AgriTextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated transport",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "− ₹$estCostPerQ/q",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFC53030)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Other known costs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "− ₹$otherCostsPerQ/q",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFC53030)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated in your pocket",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenText
                        )
                        Text(
                            text = "₹${numberFormat.format(pocketPerQ)}/q",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgriGreenPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "For $quantity quintals",
                            style = MaterialTheme.typography.bodySmall,
                            color = AgriTextSecondary
                        )
                        Text(
                            text = "₹${numberFormat.format(totalPocket)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AgriGreenPrimary
                        )
                    }
                }
            }

            // Nearby Transport Options
            Text(
                text = stringResource(R.string.nearby_transport_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )

            transporterOptions.forEachIndexed { index, option ->
                val isSelected = (selectedOption?.id == option.id)
                TransportOptionCard(
                    option = option,
                    isSelected = isSelected,
                    onSelect = { selectedOption = option },
                    numberFormat = numberFormat,
                    testTagIndex = index
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Book / Arrange Transport CTA
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_book_transport"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_book_transport),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Developer / Simulation trigger for Transport Unavailability
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isSimulatingUnavailable = true }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Simulate transport unavailability (test exception mode)",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextMuted
                )
            }
        }
    }

    // Explicit Confirmation Dialog
    if (showConfirmDialog) {
        val transporterToConfirm = selectedOption ?: transporterOptions.firstOrNull()
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.dialog_confirm_transport_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_confirm_transport_content"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Vehicle:", color = AgriTextSecondary)
                        Text(
                            text = transporterToConfirm?.name ?: "Truck",
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated cost:", color = AgriTextSecondary)
                        Text(
                            text = "₹${numberFormat.format(transporterToConfirm?.totalCost ?: estCost)}",
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pickup:", color = AgriTextSecondary)
                        Text(text = "Your location", fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Delivery:", color = AgriTextSecondary)
                        Text(text = buyerName, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Produce:", color = AgriTextSecondary)
                        Text(text = "${stringResource(cropRes)} — $quantity quintals", fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated delivery:", color = AgriTextSecondary)
                        Text(
                            text = stringResource(transporterToConfirm?.deliveryTimingRes ?: R.string.delivery_today),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.dialog_confirm_transport_msg),
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        scope.launch {
                            val tId = transporterToConfirm?.id ?: "transporter_truck"
                            repository.bookTransport(transactionId, tId)
                            isBooked = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    modifier = Modifier.testTag("btn_dialog_confirm_transport")
                ) {
                    Text(
                        text = stringResource(R.string.btn_confirm_transport),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    modifier = Modifier.testTag("btn_dialog_cancel_transport")
                ) {
                    Text(
                        text = stringResource(R.string.btn_go_back),
                        color = AgriTextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )
    }
}

@Composable
fun TransportOptionCard(
    option: TransporterOption,
    isSelected: Boolean,
    onSelect: () -> Unit,
    numberFormat: NumberFormat,
    testTagIndex: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AgriGreenPrimary else AgriCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("transport_option_$testTagIndex"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AgriGreenContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = if (isSelected) AgriGreenPrimary else AgriTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                Button(
                    onClick = onSelect,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_select_transport_$testTagIndex"),
                    shape = RoundedCornerShape(8.dp),
                    colors = if (isSelected) {
                        ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = AgriTextPrimary
                        )
                    }
                ) {
                    Text(
                        text = if (isSelected) stringResource(R.string.btn_selected) else stringResource(R.string.btn_select),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Capacity: ${option.capacityQuintals} quintals",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )
                Text(
                    text = "Estimated cost: ₹${numberFormat.format(option.totalCost)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery: ${stringResource(option.deliveryTimingRes)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AgriTextPrimary
                )
                Text(
                    text = "₹${option.costPerQuintal}/q",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )
            }
        }
    }
}
