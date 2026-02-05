package com.quickgrocery.ui.cart

import android.util.Log
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
    private val showOrderError = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)

    val uiState: StateFlow<CartUiState> = combine(
        repository.cart,
        showOrderSuccess,
        showOrderError,
        isLoading
    ) { items, showSuccess, showError, loading ->
        val total = items.sumOf { it.product.price * it.quantity }
        CartUiState(
            items = items,
            total = total,
            showOrderSuccess = showSuccess,
            showOrderError = showError,
            isLoading = loading
        )
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
            if (uiState.value.items.isNotEmpty() && !isLoading.value) {
                Log.d("CartViewModel", "Placing order...")
                isLoading.value = true
                val success = repository.placeOrder(
                    shippingAddress = "221B Baker Street, Sector 21"
                )
                isLoading.value = false
                Log.d("CartViewModel", "Order result: $success")
                if (success) {
                    showOrderSuccess.value = true
                } else {
                    showOrderError.value = true
                }
            }
        }
    }

    fun dismissOrderSuccess() {
        showOrderSuccess.update { false }
    }

    fun dismissOrderError() {
        showOrderError.update { false }
    }
}

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val showOrderSuccess: Boolean = false,
    val showOrderError: Boolean = false,
    val isLoading: Boolean = false
)
