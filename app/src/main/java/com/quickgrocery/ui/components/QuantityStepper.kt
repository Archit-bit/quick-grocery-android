package com.quickgrocery.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickgrocery.ui.theme.AppShapes
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.GreenPrimary

@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    filled: Boolean = false
) {
    val height = if (compact) 28.dp else 36.dp
    val minWidth = if (compact) 80.dp else 96.dp
    val background = if (filled) GreenPrimary else MaterialTheme.colorScheme.surface
    val contentColor = if (filled) Color.White else MaterialTheme.colorScheme.onSurface
    val border = if (filled) null else BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))

    Surface(
        shape = AppShapes.small,
        color = background,
        tonalElevation = 0.dp,
        border = border,
        modifier = modifier
            .height(height)
            .widthIn(min = minWidth)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = contentColor
                )
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = contentColor
            )
            IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = contentColor
                )
            }
        }
    }
}
