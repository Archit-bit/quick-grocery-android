package com.quickgrocery.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.QuickGroceryTheme
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onLoginClick = onLoginClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val quickTabs = remember {
        listOf(
            QuickTab("All", Icons.Default.Category),
            QuickTab("Valentine's", Icons.Default.Favorite),
            QuickTab("Winter", Icons.Default.Spa),
            QuickTab("Electronics", Icons.Default.Search),
            QuickTab("Beauty", Icons.Default.Person)
        )
    }
    val navItems = remember {
        listOf(
            BlinkitNavItem("Home", Icons.Default.Home),
            BlinkitNavItem("Categories", Icons.Default.Category),
            BlinkitNavItem("Search", Icons.Default.Search),
            BlinkitNavItem("Cart", Icons.Default.ShoppingCart),
            BlinkitNavItem("Profile", Icons.Default.Person)
        )
    }
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    val showBackToTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 6 }
    }

    var selectedTab by remember { mutableStateOf(0) }

    val fallbackTiles = remember {
        listOf(
            "Vegetables & Fruits",
            "Atta, Rice & Dal",
            "Oil, Ghee & Masala",
            "Dairy, Bread & Eggs",
            "Bakery & Biscuits",
            "Dry Fruits & Cereals",
            "Meat & Fish",
            "Kitchen & Dining",
            "Chips & Namkeen",
            "Biscuits & Cookies",
            "Chocolates",
            "Ice Creams",
            "Cold Drinks",
            "Juices",
            "Tea & Coffee",
            "Instant Food"
        )
    }
    val query = state.searchQuery.trim()
    val tiles = if (state.products.isNotEmpty()) {
        state.products.map { TileItem(label = it.name, productId = it.id) }
    } else {
        fallbackTiles
            .filter { it.contains(query, ignoreCase = true) || query.isEmpty() }
            .map { TileItem(label = it, productId = null) }
    }
    val firstSection = tiles.take(8)
    val secondSection = tiles.drop(8)

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column {
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)) {
                        BlinkitSearchBar(
                            value = state.searchQuery,
                            onValueChange = onSearchQueryChange
                        )
                    }
                    QuickTabsRow(
                        tabs = quickTabs,
                        selectedIndex = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
        },
        bottomBar = {
            BlinkitBottomNav(
                items = navItems,
                selectedIndex = 1,
                onItemSelected = { index ->
                    if (index == 3) {
                        onCartClick()
                    } else if (index == 4) {
                        onLoginClick()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(
                    start = AppSpacing.md,
                    end = AppSpacing.md,
                    top = AppSpacing.sm,
                    bottom = 88.dp
                ),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Grocery & Kitchen",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                    )
                }
                items(firstSection.size) { index ->
                    val tile = firstSection[index]
                    CategoryTile(
                        label = tile.label,
                        onClick = tile.productId?.let { id -> { onProductClick(id) } }
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Snacks & Drinks",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
                    )
                }
                items(secondSection.size) { index ->
                    val tile = secondSection[index]
                    CategoryTile(
                        label = tile.label,
                        onClick = tile.productId?.let { id -> { onProductClick(id) } }
                    )
                }
            }

            AnimatedVisibility(
                visible = showBackToTop,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = AppSpacing.md, bottom = 96.dp)
            ) {
                BackToTopPill(onClick = {
                    scope.launch { gridState.animateScrollToItem(0) }
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    QuickGroceryTheme {
        HomeScreen(
            state = HomeUiState(),
            onSearchQueryChange = {},
            onProductClick = {},
            onCartClick = {},
            onLoginClick = {}
        )
    }
}

data class TileItem(
    val label: String,
    val productId: String?
)
