package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

@Composable
fun QuickActionGrid(
    onCheckPricesClicked: () -> Unit,
    onSellProduceClicked: () -> Unit,
    onMyLotsClicked: () -> Unit,
    onGetHelpClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_hierarchy_step4),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AgriTextSecondary,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sell Produce (Strongest visual emphasis)
            QuickActionButton(
                title = stringResource(R.string.action_sell_produce),
                icon = Icons.Default.Sell,
                isPrimary = true,
                onClick = onSellProduceClicked,
                modifier = Modifier.weight(1f)
            )

            // Check Prices
            QuickActionButton(
                title = stringResource(R.string.action_check_prices),
                icon = Icons.Default.TrendingUp,
                isPrimary = false,
                onClick = onCheckPricesClicked,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // My Lots
            QuickActionButton(
                title = stringResource(R.string.action_my_lots),
                icon = Icons.Default.Inventory2,
                isPrimary = false,
                onClick = onMyLotsClicked,
                modifier = Modifier.weight(1f)
            )

            // Get Help
            QuickActionButton(
                title = stringResource(R.string.action_get_help),
                icon = Icons.Default.HelpOutline,
                isPrimary = false,
                onClick = onGetHelpClicked,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .heightIn(min = 76.dp)
            .then(
                if (!isPrimary) Modifier.border(1.5.dp, AgriCardBorder, RoundedCornerShape(14.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) AgriGreenPrimary else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimary) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isPrimary) Color.White.copy(alpha = 0.22f) else AgriGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isPrimary) Color.White else AgriGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) Color.White else AgriTextPrimary,
                maxLines = 2
            )
        }
    }
}
