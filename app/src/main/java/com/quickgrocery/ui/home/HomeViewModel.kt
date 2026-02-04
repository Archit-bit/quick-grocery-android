package com.quickgrocery.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickgrocery.data.GroceryRepository
import com.quickgrocery.data.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow("All")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.products,
        repository.cart,
        searchQuery,
        selectedCategory
    ) { products, cartItems, query, category ->
        val categories = listOf("All") + products.map { it.category }.distinct()
        val filtered = products.filter { product ->
            val matchesCategory = category == "All" || product.category == category
            val matchesQuery = product.name.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
        val cartQuantities = cartItems.associate { it.product.id to it.quantity }
        val itemCount = cartItems.sumOf { it.quantity }
        val total = cartItems.sumOf { it.product.price * it.quantity }
        HomeUiState(
            searchQuery = query,
            categories = categories,
            selectedCategory = category,
            products = filtered,
            cartQuantities = cartQuantities,
            cartItemCount = itemCount,
            cartTotal = total
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            repository.addToCart(product, 1)
        }
    }

    fun increaseQuantity(productId: String, currentQuantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(productId, currentQuantity + 1)
        }
    }

    fun decreaseQuantity(productId: String, currentQuantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(productId, currentQuantity - 1)
        }
    }
}


data class HomeUiState(
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "All",
    val products: List<Product> = emptyList(),
    val cartQuantities: Map<String, Int> = emptyMap(),
    val cartItemCount: Int = 0,
    val cartTotal: Double = 0.0
)
