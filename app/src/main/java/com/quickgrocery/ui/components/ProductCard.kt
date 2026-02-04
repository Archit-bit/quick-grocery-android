package com.quickgrocery.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickgrocery.data.Product
import com.quickgrocery.ui.theme.AppShapes
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = AppShapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                            )
                        ),
                        shape = AppShapes.small
                    )
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = product.unit,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                AnimatedContent(
                    targetState = quantity > 0,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                    },
                    label = "add_to_stepper"
                ) { inCart ->
                    if (inCart) {
                        QuantityStepper(
                            quantity = quantity,
                            onDecrease = onDecrease,
                            onIncrease = onIncrease,
                            compact = true
                        )
                    } else {
                        Button(
                            onClick = onIncrease,
                            shape = AppShapes.small,
                            contentPadding = ButtonDefaults.ContentPadding,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(text = "Add")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    QuickGroceryTheme {
        Surface(modifier = Modifier.padding(AppSpacing.lg)) {
            ProductCard(
                product = Product(
                    id = "p1",
                    name = "Organic Fresh Milk 1 L",
                    category = "Dairy",
                    price = 2.49,
                    unit = "1 L",
                    description = "Pasteurized full cream milk."
                ),
                quantity = 1,
                onIncrease = {},
                onDecrease = {},
                onClick = {}
            )
        }
    }
}
