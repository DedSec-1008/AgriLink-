package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingDown
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MarketPriceInfo
import com.example.model.ProduceItem
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGoldContainer
import com.example.ui.theme.AgriGoldSecondary
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriOnGreenContainer
import com.example.ui.theme.AgriSuccess
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary

/**
 * Step 2 in Home Hierarchy: "What I have ready to sell"
 */
@Composable
fun WhatIHaveCard(
    produce: ProduceItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_hierarchy_step1),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.8.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriGreenContainer)
                        .border(1.dp, AgriHeroGreenBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(produce.qualityRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AgriGreenContainer)
                        .border(1.dp, AgriHeroGreenBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = stringResource(produce.cropNameRes),
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(produce.cropNameRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${produce.quantityQuintals} ${stringResource(R.string.unit_quintals)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

/**
 * Step 3 in Home Hierarchy: "Today's Mandi Benchmark Price"
 */
@Composable
fun TodaysPriceCard(
    priceInfo: MarketPriceInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_hierarchy_step2),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriTextSecondary,
                    letterSpacing = 0.8.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(priceInfo.marketNameRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Baseline) {
                    Text(
                        text = "₹${priceInfo.pricePerQuintal}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriTextPrimary,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "/ quintal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextSecondary
                    )
                }

                // Price Direction Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (priceInfo.isPositiveChange) AgriGreenContainer else AgriGoldContainer)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = if (priceInfo.isPositiveChange) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (priceInfo.isPositiveChange) AgriSuccess else AgriGoldSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(priceInfo.priceChangeTextRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (priceInfo.isPositiveChange) AgriGreenPrimary else AgriGoldSecondary
                    )
                }
            }
        }
    }
}

/**
 * Combined card kept for backward compatibility
 */
@Composable
fun ProduceSummaryCard(
    produce: ProduceItem,
    priceInfo: MarketPriceInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        WhatIHaveCard(produce = produce)
        TodaysPriceCard(priceInfo = priceInfo)
    }
}
