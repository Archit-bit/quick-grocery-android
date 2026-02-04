package com.quickgrocery.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickgrocery.data.GroceryRepository
import com.quickgrocery.data.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState(isLoading = true))
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            val product = repository.getProduct(productId)
            _uiState.update { current ->
                current.copy(product = product, isLoading = false)
            }
        }
    }

    fun increment() {
        _uiState.update { current ->
            current.copy(quantity = current.quantity + 1)
        }
    }

    fun decrement() {
        _uiState.update { current ->
            current.copy(quantity = (current.quantity - 1).coerceAtLeast(1))
        }
    }

    fun addToCart() {
        val product = _uiState.value.product ?: return
        val quantity = _uiState.value.quantity
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }
}

data class ProductDetailUiState(
    val product: Product? = null,
    val quantity: Int = 1,
    val isLoading: Boolean = false
)
