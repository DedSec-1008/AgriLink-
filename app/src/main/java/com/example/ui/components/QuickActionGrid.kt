package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
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

/**
 * Farmer Quick Actions:
 * Compact, focused, non-overwhelming 3 essential shortcuts:
 * 1. 💰 भाव देखें (Check Prices)
 * 2. 📦 मेरी फसल (My Lots / Produce)
 * 3. ❓ मदद (Help)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionGrid(
    onCheckPricesClicked: () -> Unit,
    onMyLotsClicked: () -> Unit,
    onGetHelpClicked: () -> Unit,
    onSellProduceClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.section_quick_actions),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AgriTextPrimary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // 3 primary farmer quick actions in a responsive row/flow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Check Prices (भाव देखें)
            QuickActionTile(
                title = stringResource(R.string.quick_prices_label),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                onClick = onCheckPricesClicked,
                testTag = "quick_action_prices",
                modifier = Modifier.weight(1f)
            )

            // 2. My Lots / Produce (मेरी फसल)
            QuickActionTile(
                title = stringResource(R.string.quick_lots_label),
                icon = Icons.Default.Inventory2,
                onClick = onMyLotsClicked,
                testTag = "quick_action_lots",
                modifier = Modifier.weight(1f)
            )

            // 3. Get Help (मदद)
            QuickActionTile(
                title = stringResource(R.string.quick_help_label),
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                onClick = onGetHelpClicked,
                testTag = "quick_action_help",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .heightIn(min = 80.dp)
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AgriGreenContainer)
                    .border(1.dp, AgriCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary,
                maxLines = 1
            )
        }
    }
}
