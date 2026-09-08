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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.model.AgriTransaction
import com.example.model.TransactionStatus
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenText
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionDetailScreen(
    transactionId: String,
    repository: AgriRepository,
    onBack: () -> Unit,
    onNavigateToLogistics: (String) -> Unit,
    onNavigateToPickup: (String) -> Unit,
    onNavigateToDelivery: (String) -> Unit,
    onNavigateToPayment: (String) -> Unit,
    onNavigateToCompleted: (String) -> Unit,
    onNavigateToYourSales: () -> Unit,
    onGoToMyLots: () -> Unit,
    onGoToHome: () -> Unit,
    snackbarHostState: SnackbarHostState? = null,
    modifier: Modifier = Modifier
) {
    val transactions by repository.getActiveTransactions().collectAsState(initial = emptyList())
    val transaction = transactions.find { it.id == transactionId }
        ?: repository.getTransactionById(transactionId)
        ?: AgriTransaction(
            id = transactionId,
            lotId = "Lot #AG-1024",
            offerId = "offer_1024",
            buyerId = "buyer_abc",
            buyerNameRes = R.string.buyer_abc_foods,
            cropNameRes = R.string.crop_soybean,
            iconEmoji = "🌱",
            quantityQuintals = 50,
            agreedPricePerQ = 4850,
            estimatedNetAmount = 235000,
            status = TransactionStatus.OFFER_ACCEPTED
        )

    val scope = rememberCoroutineScope()
    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = numberFormat.format(transaction.agreedPricePerQ)
    val formattedNet = numberFormat.format(transaction.estimatedNetAmount)
    val grossVal = transaction.quantityQuintals * transaction.agreedPricePerQ
    val formattedGross = numberFormat.format(grossVal)
    val transportCost = 6000
    val otherCost = 1500

    var showGrievanceDialog by remember { mutableStateOf(false) }
    var selectedGrievanceType by remember { mutableStateOf(R.string.grievance_type_payment) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("transaction_detail_screen")
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
                    .background(Color.White)
                    .clickable { onBack() }
                    .testTag("btn_back_tx_detail"),
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
                    text = stringResource(R.string.title_transaction_detail),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextPrimary
                )
                Text(
                    text = "ID: ${transaction.id} • ${stringResource(transaction.status.labelRes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Section 11: Vertical Progress Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_what_has_happened),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Step 1: Offer Accepted
                VerticalProgressStep(
                    title = stringResource(R.string.step_offer_accepted),
                    subtitle = "Agreed with ${stringResource(transaction.buyerNameRes)} at ₹$formattedPrice/q",
                    stepState = StepDisplayState.COMPLETED,
                    isLast = false
                )

                // Step 2: Arrange Transport
                val transportState = when {
                    transaction.status == TransactionStatus.OFFER_ACCEPTED -> StepDisplayState.CURRENT
                    transaction.status == TransactionStatus.CANCELLED -> StepDisplayState.PENDING
                    else -> StepDisplayState.COMPLETED
                }
                VerticalProgressStep(
                    title = stringResource(R.string.step_arrange_transport),
                    subtitle = if (transportState == StepDisplayState.COMPLETED) "Transport booked with Shree Agro Transport" else "Select trusted local transport partner",
                    stepState = transportState,
                    isLast = false
                )

                // Step 3: Produce Picked Up
                val pickupState = when {
                    transaction.status == TransactionStatus.OFFER_ACCEPTED -> StepDisplayState.PENDING
                    transaction.status == TransactionStatus.LOGISTICS_BOOKED -> StepDisplayState.CURRENT
                    transaction.status == TransactionStatus.CANCELLED -> StepDisplayState.PENDING
                    else -> StepDisplayState.COMPLETED
                }
                VerticalProgressStep(
                    title = stringResource(R.string.step_produce_picked_up),
                    subtitle = if (pickupState == StepDisplayState.COMPLETED) "Produce picked up from Nagpur" else "Driver will arrive for scheduled pickup",
                    stepState = pickupState,
                    isLast = false
                )

                // Step 4: Delivered
                val deliveryState = when {
                    transaction.status in listOf(TransactionStatus.OFFER_ACCEPTED, TransactionStatus.LOGISTICS_BOOKED) -> StepDisplayState.PENDING
                    transaction.status == TransactionStatus.DISPATCHED -> StepDisplayState.CURRENT
                    transaction.status == TransactionStatus.CANCELLED -> StepDisplayState.PENDING
                    else -> StepDisplayState.COMPLETED
                }
                VerticalProgressStep(
                    title = stringResource(R.string.step_delivered),
                    subtitle = if (deliveryState == StepDisplayState.COMPLETED) "Delivered to buyer destination" else "Transporter en-route to buyer",
                    stepState = deliveryState,
                    isLast = false
                )

                // Step 5: Payment Received
                val paymentState = when {
                    transaction.status in listOf(TransactionStatus.PAYMENT_RECEIVED, TransactionStatus.COMPLETED) -> StepDisplayState.COMPLETED
                    transaction.status == TransactionStatus.PAYMENT_DELAYED -> StepDisplayState.DELAYED
                    transaction.status in listOf(TransactionStatus.DELIVERED, TransactionStatus.PAYMENT_INITIATED) -> StepDisplayState.CURRENT
                    else -> StepDisplayState.PENDING
                }
                VerticalProgressStep(
                    title = stringResource(R.string.step_payment_received),
                    subtitle = when (paymentState) {
                        StepDisplayState.COMPLETED -> "₹$formattedNet received via bank transfer"
                        StepDisplayState.DELAYED -> "Payment delayed · Assistance available"
                        StepDisplayState.CURRENT -> "Payment on the way from buyer"
                        else -> "Expected net: ₹$formattedNet"
                    },
                    stepState = paymentState,
                    isLast = false
                )

                // Step 6: Sale Completed
                val completedState = if (transaction.status == TransactionStatus.COMPLETED || transaction.status == TransactionStatus.PAYMENT_RECEIVED) {
                    StepDisplayState.COMPLETED
                } else {
                    StepDisplayState.PENDING
                }
                VerticalProgressStep(
                    title = stringResource(R.string.step_completed),
                    subtitle = if (completedState == StepDisplayState.COMPLETED) "Transaction concluded successfully" else "Final receipt & buyer feedback",
                    stepState = completedState,
                    isLast = true
                )
            }
        }

        // Section 12: Prominent "What Should I Do Next?" Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AgriHeroGreenBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_what_next),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenText,
                    letterSpacing = 0.5.sp
                )

                val (actionTitle, actionDesc, actionButtonText, actionClick) = when (transaction.status) {
                    TransactionStatus.OFFER_ACCEPTED -> Quadruple(
                        stringResource(R.string.next_step_arrange_transport),
                        "Book reliable transport to deliver produce safely to ${stringResource(transaction.buyerNameRes)}.",
                        stringResource(R.string.btn_arrange_transport)
                    ) { onNavigateToLogistics(transaction.id) }

                    TransactionStatus.LOGISTICS_BOOKED -> Quadruple(
                        stringResource(R.string.action_confirm_pickup),
                        "Transporter is scheduled for pickup. Confirm once produce is loaded.",
                        stringResource(R.string.btn_confirm_pickup)
                    ) { onNavigateToPickup(transaction.id) }

                    TransactionStatus.DISPATCHED -> Quadruple(
                        stringResource(R.string.action_track_delivery),
                        "Produce is in transit to ${stringResource(transaction.buyerNameRes)}. Confirm upon arrival.",
                        stringResource(R.string.action_track_delivery)
                    ) { onNavigateToDelivery(transaction.id) }

                    TransactionStatus.DELIVERED, TransactionStatus.PAYMENT_INITIATED -> Quadruple(
                        stringResource(R.string.action_track_payment),
                        "Produce delivered. Track the payment transfer from buyer.",
                        stringResource(R.string.action_track_payment)
                    ) { onNavigateToPayment(transaction.id) }

                    TransactionStatus.PAYMENT_DELAYED -> Quadruple(
                        stringResource(R.string.payment_delayed_header),
                        "Payment has not arrived yet. You can get support or record payment if received.",
                        stringResource(R.string.action_track_payment)
                    ) { onNavigateToPayment(transaction.id) }

                    TransactionStatus.PAYMENT_RECEIVED, TransactionStatus.COMPLETED -> Quadruple(
                        stringResource(R.string.title_sale_completed),
                        "Sale has been completed and payment received. Rate the buyer or view receipt.",
                        stringResource(R.string.action_view_completed)
                    ) { onNavigateToCompleted(transaction.id) }

                    else -> Quadruple(
                        "Review Details",
                        "View full transaction log and support options.",
                        "View Details"
                    ) { }
                }

                Text(
                    text = actionTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Text(
                    text = actionDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AgriTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = actionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_next_action"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(
                        text = actionButtonText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Deal Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buyer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(transaction.buyerNameRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
                        text = "${stringResource(transaction.cropNameRes)} · ${transaction.quantityQuintals} quintals",
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
                        text = "Agreed Price",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹$formattedPrice / quintal",
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
                        text = stringResource(R.string.label_net_in_pocket),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenText
                    )
                    Text(
                        text = "₹$formattedNet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                }
            }
        }

        // Transparent Calculation Breakdown (Section 27)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.payment_breakdown_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_produce_value, transaction.quantityQuintals, formattedPrice),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹$formattedGross",
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
                        text = stringResource(R.string.label_deduction_transport),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "− ₹${numberFormat.format(transportCost)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_deduction_other),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "− ₹${numberFormat.format(otherCost)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626)
                    )
                }

                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_net_in_pocket),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenText
                    )
                    Text(
                        text = "₹$formattedNet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                }

                Text(
                    text = stringResource(R.string.label_payment_deduction_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Grievance / Assistance Section (Section 20)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.grievance_prompt),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                    Text(
                        text = "AgriLink officers are on standby to resolve disputes or delays",
                        style = MaterialTheme.typography.bodySmall,
                        color = AgriTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedButton(
                    onClick = { showGrievanceDialog = true },
                    modifier = Modifier.testTag("btn_get_help_tx_detail"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.btn_get_help), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Secondary Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToYourSales,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_all_sales"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.title_your_sales),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onGoToMyLots,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_tx_to_my_lots"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_back_to_my_lots),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Grievance Dialog
    if (showGrievanceDialog) {
        val grievanceOptions = listOf(
            R.string.grievance_type_payment,
            R.string.grievance_type_buyer,
            R.string.grievance_type_delivery,
            R.string.grievance_type_transport,
            R.string.grievance_type_quality,
            R.string.grievance_type_other
        )

        AlertDialog(
            onDismissRequest = { showGrievanceDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.grievance_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    grievanceOptions.forEach { typeRes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedGrievanceType = typeRes }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedGrievanceType == typeRes),
                                onClick = { selectedGrievanceType = typeRes }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(typeRes),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.submitGrievance(transaction.id, selectedGrievanceType, "Help requested from Transaction Detail")
                            showGrievanceDialog = false
                            snackbarHostState?.showSnackbar(
                                message = "Assistance request recorded. AgriLink officer will follow up within 2 hours."
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(text = "Submit Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGrievanceDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

private enum class StepDisplayState {
    COMPLETED,
    CURRENT,
    DELAYED,
    PENDING
}

@Composable
private fun VerticalProgressStep(
    title: String,
    subtitle: String,
    stepState: StepDisplayState,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (stepState) {
                            StepDisplayState.COMPLETED -> AgriGreenPrimary
                            StepDisplayState.CURRENT -> AgriGreenPrimary
                            StepDisplayState.DELAYED -> Color(0xFFEA580C)
                            StepDisplayState.PENDING -> Color(0xFFE0E0E0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (stepState) {
                    StepDisplayState.COMPLETED -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    StepDisplayState.CURRENT -> Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    StepDisplayState.DELAYED -> Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    StepDisplayState.PENDING -> Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(
                            if (stepState == StepDisplayState.COMPLETED) AgriGreenPrimary else Color(0xFFE0E0E0)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (stepState == StepDisplayState.CURRENT || stepState == StepDisplayState.COMPLETED) FontWeight.Bold else FontWeight.Medium,
                color = when (stepState) {
                    StepDisplayState.COMPLETED -> AgriGreenPrimary
                    StepDisplayState.CURRENT -> AgriTextPrimary
                    StepDisplayState.DELAYED -> Color(0xFFEA580C)
                    StepDisplayState.PENDING -> AgriTextMuted
                }
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AgriTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
