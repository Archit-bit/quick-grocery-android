package com.quickgrocery.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickgrocery.data.CartItem
import com.quickgrocery.data.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {
    private val showOrderSuccess = MutableStateFlow(false)

    val uiState: StateFlow<CartUiState> = combine(
        repository.cart,
        showOrderSuccess
    ) { items, showSuccess ->
        val total = items.sumOf { it.product.price * it.quantity }
        CartUiState(items = items, total = total, showOrderSuccess = showSuccess)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartUiState())

    fun increase(item: CartItem) {
        viewModelScope.launch {
            repository.updateQuantity(item.product.id, item.quantity + 1)
        }
    }

    fun decrease(item: CartItem) {
        viewModelScope.launch {
            repository.updateQuantity(item.product.id, item.quantity - 1)
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            if (uiState.value.items.isNotEmpty()) {
                repository.clearCart()
                showOrderSuccess.value = true
            }
        }
    }

    fun dismissOrderSuccess() {
        showOrderSuccess.update { false }
    }
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val showOrderSuccess: Boolean = false
)
