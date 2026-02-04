package com.quickgrocery.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickgrocery.data.Product
import com.quickgrocery.ui.components.CategoryChips
import com.quickgrocery.ui.components.ProductCard
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun HomeRoute(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onAddToCart = viewModel::addToCart,
        onIncreaseQuantity = viewModel::increaseQuantity,
        onDecreaseQuantity = viewModel::decreaseQuantity,
        onProductClick = onProductClick,
        onCartClick = onCartClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit
) {
    val promoBanners = remember {
        listOf(
            PromoBanner(
                title = "Fresh deals",
                subtitle = "Up to 40% off",
                cta = "Shop now",
                colorStart = androidx.compose.ui.graphics.Color(0xFF3A7DFF),
                colorEnd = androidx.compose.ui.graphics.Color(0xFF7FB5FF)
            ),
            PromoBanner(
                title = "Snacks rush",
                subtitle = "2 for 1 today",
                cta = "Grab it",
                colorStart = androidx.compose.ui.graphics.Color(0xFFFF8A00),
                colorEnd = androidx.compose.ui.graphics.Color(0xFFFFC857)
            ),
            PromoBanner(
                title = "Daily essentials",
                subtitle = "Lowest prices",
                cta = "See more",
                colorStart = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                colorEnd = androidx.compose.ui.graphics.Color(0xFF66BB6A)
            )
        )
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onProfileClick = {}
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.cartItemCount > 0,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                BottomCartBar(
                    itemCount = state.cartItemCount,
                    total = state.cartTotal,
                    onViewCart = onCartClick
                )
            }
        }
    ) { padding ->
        val bottomPadding = if (state.cartItemCount > 0) 96.dp else AppSpacing.lg
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = AppSpacing.lg,
                end = AppSpacing.lg,
                top = AppSpacing.md,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PromoCarousel(banners = promoBanners)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Shop by category",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryChips(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Popular picks",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.products, key = { it.id }) { product ->
                val quantity = state.cartQuantities[product.id] ?: 0
                ProductCard(
                    product = product,
                    quantity = quantity,
                    onIncrease = {
                        if (quantity == 0) {
                            onAddToCart(product)
                        } else {
                            onIncreaseQuantity(product.id, quantity)
                        }
                    },
                    onDecrease = {
                        onDecreaseQuantity(product.id, quantity)
                    },
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    QuickGroceryTheme {
        HomeScreen(
            state = HomeUiState(
                searchQuery = "",
                categories = listOf("All", "Dairy", "Fruits"),
                selectedCategory = "All",
                products = listOf(
                    Product(
                        id = "p1",
                        name = "Fresh Milk",
                        category = "Dairy",
                        price = 2.49,
                        unit = "1 L",
                        description = "Pasteurized full cream milk."
                    ),
                    Product(
                        id = "p2",
                        name = "Bananas",
                        category = "Fruits",
                        price = 0.59,
                        unit = "1 lb",
                        description = "Naturally sweet bananas."
                    )
                ),
                cartQuantities = mapOf("p1" to 1),
                cartItemCount = 1,
                cartTotal = 2.49
            ),
            onSearchQueryChange = {},
            onCategorySelected = {},
            onAddToCart = {},
            onIncreaseQuantity = { _, _ -> },
            onDecreaseQuantity = { _, _ -> },
            onProductClick = {},
            onCartClick = {}
        )
    }
}
