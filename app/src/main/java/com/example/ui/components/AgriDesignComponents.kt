package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgriCardBorder
import com.example.ui.theme.AgriDivider
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
 * Standard tactile primary button for Indian farmers.
 * Ensures minimum 52dp touch target, high daylight contrast, bold readable text,
 * and graceful multi-line wrapping under system font scaling up to 150%.
 */
@Composable
fun AgriPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    showTrailingArrow: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    minHeight: Dp = 54.dp
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = minHeight)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
            if (showTrailingArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Clean secondary outlined button with 48dp+ target and tactile border.
 */
@Composable
fun AgriSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    minHeight: Dp = 48.dp
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = minHeight)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Standard elevated card with crisp outdoor border and warm feel.
 */
@Composable
fun AgriCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = AgriCardBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

/**
 * Status badge with clear contrast for farmers.
 */
@Composable
fun AgriStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = AgriGreenContainer,
    textColor: Color = AgriGreenPrimary,
    borderColor: Color? = AgriHeroGreenBorder,
    icon: ImageVector? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
                else Modifier
            )
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Visual step progression indicator for workflows (Selling Wizard, Delivery, Payments).
 * Easy to interpret with numbers, checkmarks, and clear step states.
 */
@Composable
fun AgriWorkflowProgress(
    totalSteps: Int,
    currentStep: Int,
    stepLabels: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (step in 1..totalSteps) {
                val isCompleted = step < currentStep
                val isActive = step == currentStep

                // Step Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> AgriSuccess
                                isActive -> AgriGreenPrimary
                                else -> AgriGreenContainer
                            }
                        )
                        .border(
                            width = if (isActive) 2.dp else 1.dp,
                            color = when {
                                isCompleted -> AgriSuccess
                                isActive -> AgriHeroGreenBorder
                                else -> AgriCardBorder
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Step $step completed",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "$step",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary else AgriTextMuted
                        )
                    }
                }

                // Connecting Line between circles
                if (step < totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .padding(horizontal = 4.dp)
                            .background(
                                if (step < currentStep) AgriSuccess else AgriDivider,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Active Step Title
        if (currentStep in 1..stepLabels.size) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step $currentStep of $totalSteps",
                    style = MaterialTheme.typography.labelSmall,
                    color = AgriGreenPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stepLabels[currentStep - 1],
                    style = MaterialTheme.typography.labelMedium,
                    color = AgriTextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

/**
 * Transparent Price & Payout Highlight Card.
 * Clearly separates Quoted Rate vs Deductions vs Final In-Pocket Amount.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgriNetPayoutHighlight(
    quotedRate: String,
    netRate: String,
    estimatedTotal: String,
    deductionNote: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AgriGreenContainer)
            .border(1.5.dp, AgriHeroGreenBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = "नक्त हातात मिळणारा भाव / Net in your pocket",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AgriGreenPrimary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = netRate,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "/ क्विंटल",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "अंदाजे एकूण रक्कम",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = AgriTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = estimatedTotal,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGoldSecondary,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = AgriHeroGreenBorder.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "मूळ दिलेला भाव: $quotedRate",
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = deductionNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = AgriTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
