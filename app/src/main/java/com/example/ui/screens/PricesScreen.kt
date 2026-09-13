package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AgriRepository
import com.example.model.CropOption
import com.example.model.MarketPriceInfo
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriGoldContainer
import com.example.ui.theme.AgriGoldSecondary
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriHeroGreenBorder
import com.example.ui.theme.AgriOnGoldContainer
import com.example.ui.theme.AgriOnGreenContainer
import com.example.ui.theme.AgriSuccess
import com.example.ui.theme.AgriSurface
import com.example.ui.theme.AgriTextMuted
import com.example.ui.theme.AgriTextPrimary
import com.example.ui.theme.AgriTextSecondary
import com.example.ui.theme.AgriWarning
import kotlin.math.roundToInt

/**
 * Data holder for comparing a mandi against the primary benchmark.
 */
data class MarketComparisonItem(
    val id: String,
    val marketNameRes: Int,
    val cropNameRes: Int,
    val pricePerQuintal: Int,
    val priceChangeTextRes: Int,
    val isPositiveChange: Boolean,
    val distanceKm: Int?, // null means primary/nearby
    val diffFromBenchmark: Int, // 0 = benchmark, positive = lower than benchmark
    val isBenchmark: Boolean = false
)

@Composable
fun PricesScreen(
    repository: AgriRepository,
    onNavigateToSell: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }

    // Preserve selected crop across configuration changes / screen rotations
    var selectedCropId by rememberSaveable { mutableStateOf("soybean") }

    val availableCrops = remember { repository.getAvailableCrops() }
    val baseMarketPrices = remember { repository.getMarketPrices() }
    val basePrimaryPrice = remember { repository.getTodaysPrimaryPrice() }

    // Selected crop details
    val selectedCrop = availableCrops.find { it.id == selectedCropId } ?: availableCrops.first()

    // Calculate crop-adjusted benchmark and market rates
    val priceMultiplier = selectedCrop.typicalPrice.toDouble() / 4850.0

    val primaryPrice = remember(selectedCropId, basePrimaryPrice) {
        val calculatedPrice = (basePrimaryPrice.pricePerQuintal * priceMultiplier).roundToInt()
        basePrimaryPrice.copy(
            cropNameRes = selectedCrop.nameRes,
            pricePerQuintal = calculatedPrice
        )
    }

    // Distances mapped to existing market entries
    val marketDistances = remember {
        mapOf(
            "1" to null, // Nagpur APMC (Benchmark / Nearby)
            "2" to 18,   // Katol Market
            "3" to 12,   // Hingna Market
            "4" to 45    // Amravati APMC
        )
    }

    // Benchmark comparison list
    val comparisonList = remember(selectedCropId, baseMarketPrices, primaryPrice) {
        baseMarketPrices.map { market ->
            val adjustedPrice = (market.pricePerQuintal * priceMultiplier).roundToInt()
            val diff = primaryPrice.pricePerQuintal - adjustedPrice
            val isBench = market.id == "1" || diff == 0

            MarketComparisonItem(
                id = market.id,
                marketNameRes = market.marketNameRes,
                cropNameRes = selectedCrop.nameRes,
                pricePerQuintal = adjustedPrice,
                priceChangeTextRes = market.priceChangeTextRes,
                isPositiveChange = market.isPositiveChange,
                distanceKm = marketDistances[market.id],
                diffFromBenchmark = diff,
                isBenchmark = isBench
            )
        }
    }

    // 7-day trend data points (Day 1 to Today)
    val trendPoints = remember(primaryPrice.pricePerQuintal) {
        val p = primaryPrice.pricePerQuintal
        listOf(
            p - 120, // 6 days ago (Mon)
            p - 100, // 5 days ago (Tue)
            p - 110, // 4 days ago (Wed)
            p - 70,  // 3 days ago (Thu)
            p - 50,  // 2 days ago (Fri)
            p - 20,  // Yesterday (Sat)
            p        // Today
        )
    }

    // =========================================================================
    // SECTION COMPOSABLES (Reusable across Portrait and Landscape layouts)
    // =========================================================================

    val headerSection = @Composable {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.title_prices),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AgriTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.subtitle_prices),
                style = MaterialTheme.typography.bodyMedium,
                color = AgriTextSecondary
            )
        }
    }

    val cropSelectorSection = @Composable {
        CropSelectorRow(
            crops = availableCrops,
            selectedCropId = selectedCropId,
            onCropSelected = { selectedCropId = it }
        )
    }

    val benchmarkPriceCard = @Composable {
        BenchmarkPriceHeroCard(
            primaryPrice = primaryPrice,
            cropEmoji = selectedCrop.emoji
        )
    }

    val priceTrendCard = @Composable {
        PriceTrendCard(
            trendPoints = trendPoints,
            primaryPrice = primaryPrice
        )
    }

    val nearbyMarketsSection = @Composable {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.section_nearby_mandi_rates),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriTextPrimary
            )
            comparisonList.forEach { item ->
                MandiComparisonCard(item = item)
            }
        }
    }

    val sellActionCard = @Composable {
        FindBestPlaceToSellCard(onNavigateToSell = onNavigateToSell)
    }

    // =========================================================================
    // ROOT RESPONSIVE CONTAINER
    // =========================================================================
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            headerSection()

            cropSelectorSection()

            if (isLandscape) {
                // Landscape Two-Column Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Column: Benchmark & 7-Day Trend
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        benchmarkPriceCard()
                        priceTrendCard()
                    }

                    // Right Column: Nearby Mandis & Action
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        nearbyMarketsSection()
                        sellActionCard()
                    }
                }
            } else {
                // Portrait Single-Column Hierarchy (Crop -> Price Hero -> Nearby Markets -> 7-Day Trend -> Sell Action)
                benchmarkPriceCard()
                nearbyMarketsSection()
                priceTrendCard()
                sellActionCard()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// =============================================================================
// COMPONENT 1: CROP SELECTOR
// =============================================================================

@Composable
private fun CropSelectorRow(
    crops: List<CropOption>,
    selectedCropId: String,
    onCropSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.label_select_crop),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = AgriTextSecondary
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(crops, key = { it.id }) { crop ->
                val isSelected = crop.id == selectedCropId
                val cropName = stringResource(crop.nameRes)

                Surface(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onCropSelected(crop.id) }
                        .semantics { contentDescription = cropName }
                        .testTag("crop_chip_${crop.id}"),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) AgriGreenPrimary else AgriSurface,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) AgriGreenPrimary else AgriCardBorder
                    ),
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = crop.emoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cropName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else AgriTextPrimary
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// COMPONENT 2: TOP BENCHMARK PRICE HERO CARD
// =============================================================================

@Composable
private fun BenchmarkPriceHeroCard(
    primaryPrice: MarketPriceInfo,
    cropEmoji: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriHeroGreenBorder, RoundedCornerShape(16.dp))
            .testTag("card_todays_benchmark"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Header: Category & Benchmark Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_todays_benchmark).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenPrimary,
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AgriGreenContainer)
                        .border(1.dp, AgriHeroGreenBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.badge_benchmark_market),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }
            }

            // Crop + Market Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AgriGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = cropEmoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(primaryPrice.cropNameRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AgriTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = stringResource(primaryPrice.marketNameRes),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = AgriTextSecondary
                            )
                        }
                    }
                }

                // Direction pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (primaryPrice.isPositiveChange) AgriGreenContainer else AgriGoldContainer)
                        .border(
                            width = 1.dp,
                            color = if (primaryPrice.isPositiveChange) AgriHeroGreenBorder else AgriCardBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (primaryPrice.isPositiveChange) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (primaryPrice.isPositiveChange) AgriSuccess else AgriWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(primaryPrice.priceChangeTextRes),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (primaryPrice.isPositiveChange) AgriGreenPrimary else AgriGoldSecondary
                        )
                    }
                }
            }

            // High-Visibility Price Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₹${primaryPrice.pricePerQuintal}",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 36.sp),
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary
                    )
                    Text(
                        text = "/ ${stringResource(R.string.unit_quintals)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriTextSecondary
                    )
                }

                Text(
                    text = "Nagpur APMC Modal",
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriTextMuted
                )
            }

            // Quick Decision / Meaning for Farmer
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (primaryPrice.isPositiveChange) AgriGreenContainer else AgriGoldContainer,
                border = BorderStroke(
                    1.dp,
                    if (primaryPrice.isPositiveChange) AgriHeroGreenBorder else AgriCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prices_quick_decision_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (primaryPrice.isPositiveChange) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (primaryPrice.isPositiveChange) AgriSuccess else AgriGoldSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (primaryPrice.isPositiveChange) {
                            stringResource(R.string.quick_decision_rising)
                        } else {
                            stringResource(R.string.quick_decision_falling)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (primaryPrice.isPositiveChange) AgriOnGreenContainer else AgriOnGoldContainer
                    )
                }
            }
        }
    }
}

// =============================================================================
// COMPONENT 3: 7-DAY PRICE TREND & LIGHTWEIGHT RESPONSIVE CHART
// =============================================================================

@Composable
private fun PriceTrendCard(
    trendPoints: List<Int>,
    primaryPrice: MarketPriceInfo
) {
    val dayLabels = listOf(
        stringResource(R.string.chart_day_mon),
        stringResource(R.string.chart_day_tue),
        stringResource(R.string.chart_day_wed),
        stringResource(R.string.chart_day_thu),
        stringResource(R.string.chart_day_fri),
        stringResource(R.string.chart_day_sat),
        stringResource(R.string.chart_day_today)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriCardBorder, RoundedCornerShape(16.dp))
            .testTag("card_price_trend"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_price_trend),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AgriTextPrimary
                )

                Text(
                    text = stringResource(R.string.trend_summary_nagpur),
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Plain-Language Explanation (Prominent Farmer Summary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AgriGreenContainer)
                    .border(1.dp, AgriHeroGreenBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = AgriSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.trend_increased_7d),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriOnGreenContainer
                    )
                }
            }

            // Price Labels above the chart (Start and Current)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "6d ago: ₹${trendPoints.first()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriTextMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Today: ₹${trendPoints.last()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Lightweight Responsive Canvas Chart
            TrendChartCanvas(
                points = trendPoints,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(horizontal = 8.dp)
            )

            // Day Labels Row under the chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayLabels.forEachIndexed { index, label ->
                    val isToday = index == dayLabels.size - 1
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                        color = if (isToday) AgriGreenPrimary else AgriTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Clean, lightweight responsive Compose Canvas chart.
 * Renders smooth trend line, soft gradient fill, baseline reference, and data dots.
 */
@Composable
private fun TrendChartCanvas(
    points: List<Int>,
    modifier: Modifier = Modifier
) {
    val borderColor = AgriCardBorder
    val primaryColor = AgriGreenPrimary
    val successColor = AgriSuccess
    val ringColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val w = size.width
        val h = size.height

        val minVal = (points.minOrNull() ?: 0) - 20
        val maxVal = (points.maxOrNull() ?: 1) + 20
        val valRange = (maxVal - minVal).coerceAtLeast(40).toFloat()

        val stepX = w / (points.size - 1)

        val coords = points.mapIndexed { index, value ->
            val x = index * stepX
            val ratio = (value - minVal) / valRange
            val y = h - (ratio * h)
            Offset(x, y.coerceIn(8f, h - 8f))
        }

        // 1. Draw subtle dashed baseline
        val baselineY = h - 6f
        drawLine(
            color = borderColor,
            start = Offset(0f, baselineY),
            end = Offset(w, baselineY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
        )

        // 2. Draw gradient fill area
        val fillPath = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            for (i in 1 until coords.size) {
                val prev = coords[i - 1]
                val curr = coords[i]
                val cx = (prev.x + curr.x) / 2f
                cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
            }
            lineTo(coords.last().x, h)
            lineTo(coords.first().x, h)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.22f),
                    primaryColor.copy(alpha = 0.02f)
                ),
                startY = 0f,
                endY = h
            )
        )

        // 3. Draw smooth curve stroke
        val strokePath = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            for (i in 1 until coords.size) {
                val prev = coords[i - 1]
                val curr = coords[i]
                val cx = (prev.x + curr.x) / 2f
                cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
            }
        }

        drawPath(
            path = strokePath,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // 4. Draw data point markers
        coords.forEachIndexed { index, offset ->
            val isLast = index == coords.size - 1
            val radius = if (isLast) 5.dp.toPx() else 3.5.dp.toPx()

            // Outer ring matching surface background
            drawCircle(
                color = ringColor,
                radius = radius + 2.dp.toPx(),
                center = offset
            )
            // Inner dot
            drawCircle(
                color = if (isLast) primaryColor else successColor,
                radius = radius,
                center = offset
            )
        }
    }
}

// =============================================================================
// COMPONENT 4: NEARBY MANDI COMPARISON CARDS
// =============================================================================

@Composable
private fun MandiComparisonCard(item: MarketComparisonItem) {
    val marketName = stringResource(item.marketNameRes)
    val distanceText = if (item.distanceKm != null) {
        stringResource(R.string.label_mandi_distance, item.distanceKm)
    } else {
        stringResource(R.string.market_distance_nearby)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (item.isBenchmark) 1.5.dp else 1.dp,
                color = if (item.isBenchmark) AgriHeroGreenBorder else AgriCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("card_mandi_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isBenchmark) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Market details & distance
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = marketName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.isBenchmark) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.badge_highest_rate),
                            tint = AgriSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Distance pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AgriBackground)
                            .border(0.8.dp, AgriCardBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AgriTextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = distanceText,
                                style = MaterialTheme.typography.labelSmall,
                                color = AgriTextSecondary
                            )
                        }
                    }

                    // Comparison badge (e.g. "Highest Rate" or "₹70 lower")
                    if (item.isBenchmark) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AgriGreenContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.badge_highest_rate),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                        }
                    } else if (item.diffFromBenchmark > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AgriGoldContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.price_diff_lower, item.diffFromBenchmark),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AgriGoldSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right: Price & Direction
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "₹${item.pricePerQuintal}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.isBenchmark) AgriGreenPrimary else AgriTextPrimary
                )
                Text(
                    text = "/ ${stringResource(R.string.unit_quintals)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriTextMuted
                )
                Text(
                    text = stringResource(item.priceChangeTextRes),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (item.isPositiveChange) AgriSuccess else AgriTextMuted
                )
            }
        }
    }
}

// =============================================================================
// COMPONENT 5: ACTION CARD - "WHERE SHOULD I SELL?"
// =============================================================================

@Composable
private fun FindBestPlaceToSellCard(
    onNavigateToSell: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, AgriHeroGreenBorder, RoundedCornerShape(16.dp))
            .testTag("card_prices_find_best_place"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.prices_sell_action_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AgriGreenPrimary
            )

            Text(
                text = stringResource(R.string.prices_sell_action_subtext),
                style = MaterialTheme.typography.bodyMedium,
                color = AgriOnGreenContainer,
                lineHeight = 20.sp
            )

            Button(
                onClick = onNavigateToSell,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("btn_prices_find_best_place"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AgriGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.btn_find_best_place),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
