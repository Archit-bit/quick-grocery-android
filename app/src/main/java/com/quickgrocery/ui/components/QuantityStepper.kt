package com.quickgrocery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.quickgrocery.ui.theme.AppShapes
import com.quickgrocery.ui.theme.AppSpacing

@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val height = if (compact) 32.dp else 40.dp
    Surface(
        shape = AppShapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .height(height)
            .widthIn(min = 96.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDecrease) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier.padding(2.dp)
                )
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleSmall
            )
            IconButton(onClick = onIncrease) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}
