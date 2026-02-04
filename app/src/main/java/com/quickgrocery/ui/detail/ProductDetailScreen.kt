package com.quickgrocery.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickgrocery.data.Product
import com.quickgrocery.ui.components.QuantityStepper
import com.quickgrocery.ui.components.formatPrice
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun ProductDetailRoute(
    productId: String,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }
    val state by viewModel.uiState.collectAsState()
    ProductDetailScreen(
        state = state,
        onBack = onBack,
        onGoToCart = onGoToCart,
        onDecrease = viewModel::decrement,
        onIncrease = viewModel::increment,
        onAddToCart = viewModel::addToCart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    state: ProductDetailUiState,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.product?.name ?: "Product") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        val product = state.product
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(shape = RoundedCornerShape(20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                }
            }
            if (product != null) {
                Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${product.unit} | ${formatPrice(product.price)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantityStepper(
                        quantity = state.quantity,
                        onDecrease = onDecrease,
                        onIncrease = onIncrease
                    )
                    Button(onClick = onAddToCart) {
                        Text(text = "Add to cart")
                    }
                }
            } else {
                Text(text = "Product not found.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    QuickGroceryTheme {
        ProductDetailScreen(
            state = ProductDetailUiState(
                product = Product(
                    id = "p1",
                    name = "Fresh Milk",
                    category = "Dairy",
                    price = 2.49,
                    unit = "1 L",
                    description = "Pasteurized full cream milk."
                ),
                quantity = 2
            ),
            onBack = {},
            onGoToCart = {},
            onDecrease = {},
            onIncrease = {},
            onAddToCart = {}
        )
    }
}
