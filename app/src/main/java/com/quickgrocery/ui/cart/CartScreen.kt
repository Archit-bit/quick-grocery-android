package com.quickgrocery.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickgrocery.data.CartItem
import com.quickgrocery.data.Product
import com.quickgrocery.ui.components.QuantityStepper
import com.quickgrocery.ui.components.formatPrice
import com.quickgrocery.ui.theme.AppShapes
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun CartRoute(
    onBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    CartScreen(
        state = state,
        onBack = onBack,
        onIncrease = viewModel::increase,
        onDecrease = viewModel::decrease,
        onPlaceOrder = viewModel::placeOrder,
        onDismissSuccess = viewModel::dismissOrderSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartUiState,
    onBack: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onPlaceOrder: () -> Unit,
    onDismissSuccess: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Your Cart") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.lg)
        ) {
            if (state.items.isEmpty()) {
                Text(
                    text = "Your cart is empty.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                Text(
                    text = "Add items from the home screen to get started.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    items(state.items, key = { it.product.id }) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = { onIncrease(item) },
                            onDecrease = { onDecrease(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppSpacing.lg))
                SummaryCard(total = state.total)
                Spacer(modifier = Modifier.height(AppSpacing.md))
                Button(
                    onClick = onPlaceOrder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Place order")
                }
            }
        }

        if (state.showOrderSuccess) {
            AlertDialog(
                onDismissRequest = onDismissSuccess,
                title = { Text(text = "Order placed") },
                text = { Text(text = "Your order has been placed successfully.") },
                confirmButton = {
                    TextButton(onClick = onDismissSuccess) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = AppShapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxImagePlaceholder()
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = item.product.unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = formatPrice(item.product.price),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            QuantityStepper(
                quantity = item.quantity,
                onDecrease = onDecrease,
                onIncrease = onIncrease,
                compact = true
            )
        }
    }
}

@Composable
private fun BoxImagePlaceholder() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
private fun SummaryCard(total: Double) {
    Card(
        shape = AppShapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Subtotal", style = MaterialTheme.typography.labelLarge)
                Text(text = formatPrice(total), style = MaterialTheme.typography.labelLarge)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Free",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", style = MaterialTheme.typography.titleMedium)
                Text(text = formatPrice(total), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    QuickGroceryTheme {
        CartScreen(
            state = CartUiState(
                items = listOf(
                    CartItem(
                        product = Product(
                            id = "p1",
                            name = "Fresh Milk",
                            category = "Dairy",
                            price = 2.49,
                            unit = "1 L",
                            description = "Pasteurized full cream milk."
                        ),
                        quantity = 2
                    )
                ),
                total = 4.98
            ),
            onBack = {},
            onIncrease = {},
            onDecrease = {},
            onPlaceOrder = {},
            onDismissSuccess = {}
        )
    }
}
