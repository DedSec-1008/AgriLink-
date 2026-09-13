package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenText
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AcceptOfferConfirmScreen(
    offerId: String,
    repository: AgriRepository,
    onBack: () -> Unit,
    onConfirmAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offer = repository.getOfferById(offerId) ?: return

    val numberFormat = NumberFormat.getNumberInstance(Locale("en", "IN"))
    val formattedPrice = numberFormat.format(offer.pricePerQuintal)
    val formattedNet = numberFormat.format(offer.estimatedNetAmount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("accept_offer_confirm_screen")
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
                    .testTag("btn_back_confirm_accept"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = AgriTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.title_accept_confirm),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = AgriTextPrimary
            )
        }

        Text(
            text = stringResource(R.string.accept_confirm_sub, stringResource(offer.buyerNameRes)),
            style = MaterialTheme.typography.bodyMedium,
            color = AgriTextSecondary
        )

        // Offer Terms Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Buyer Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_buyer_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = stringResource(offer.buyerNameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }

                // Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_quoted_price_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "₹$formattedPrice / ${stringResource(R.string.unit_quintals_short)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextPrimary
                    )
                }

                // Quantity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_quantity_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = "${offer.quantityQuintals} ${stringResource(R.string.unit_quintals)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextPrimary
                    )
                }

                // Payment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_payment_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AgriTextSecondary
                    )
                    Text(
                        text = stringResource(offer.paymentTermsRes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextPrimary
                    )
                }
            }
        }

        // Expected Net Payout Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_expected_amount_after_expenses),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenText,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₹$formattedNet",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.label_net_in_pocket_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )
            }
        }

        // Informational Note
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.AgriHeroGreenBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.label_offer_status_will_change, stringResource(R.string.status_offer_accepted)),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenText
                    )
                }
                Text(
                    text = stringResource(R.string.label_accept_offer_terms_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Accept Button
        Button(
            onClick = onConfirmAccept,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_confirm_accept_offer"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.btn_confirm_accept),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Go Back Button
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_back_from_confirm"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_go_back),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
