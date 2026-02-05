package com.quickgrocery.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickgrocery.data.CartItem
import com.quickgrocery.data.Product
import com.quickgrocery.data.sampleProducts
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
        onDismissSuccess = viewModel::dismissOrderSuccess,
        onDismissError = viewModel::dismissOrderError
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
    onDismissSuccess: () -> Unit,
    onDismissError: () -> Unit
) {
    val recommendations = remember { sampleProducts().shuffled().take(6) }
    val bottomPadding = if (state.items.isNotEmpty()) 140.dp else 24.dp

    Scaffold(
        topBar = {
            CheckoutHeader(onBack = onBack, onShare = {})
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Column {
                    AddressStrip(onChange = {})
                    PrimaryGreenCTA(
                        text = if (state.isLoading) "Placing order..." else "Place order",
                        onClick = onPlaceOrder,
                        enabled = !state.isLoading
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            contentPadding = PaddingValues(
                start = AppSpacing.md,
                end = AppSpacing.md,
                top = AppSpacing.sm,
                bottom = bottomPadding
            )
        ) {
            item {
                DeliveryCard()
            }
            items(state.items, key = { it.product.id }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { onIncrease(item) },
                    onDecrease = { onDecrease(item) }
                )
            }
            if (state.items.isNotEmpty()) {
                item {
                    Text(
                        text = "You might also like",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                        items(recommendations) { product ->
                            RecommendationCard(product = product)
                        }
                    }
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

        if (state.showOrderError) {
            AlertDialog(
                onDismissRequest = onDismissError,
                title = { Text(text = "Order failed") },
                text = { Text(text = "Unable to place order. Please check your connection and try again.") },
                confirmButton = {
                    TextButton(onClick = onDismissError) {
                        Text(text = "OK")
                    }
                }
            )
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
            onDismissSuccess = {},
            onDismissError = {}
        )
    }
}
