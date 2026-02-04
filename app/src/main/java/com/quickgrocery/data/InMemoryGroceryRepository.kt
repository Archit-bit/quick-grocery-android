package com.quickgrocery.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryGroceryRepository @Inject constructor() : GroceryRepository {
    private val productsFlow = MutableStateFlow(sampleProducts())
    private val cartFlow = MutableStateFlow<List<CartItem>>(emptyList())

    override val products = productsFlow.asStateFlow()
    override val cart: StateFlow<List<CartItem>> = cartFlow.asStateFlow()

    override suspend fun getProduct(productId: String): Product? {
        return productsFlow.value.firstOrNull { it.id == productId }
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        if (quantity <= 0) return
        cartFlow.update { current ->
            val existing = current.firstOrNull { it.product.id == product.id }
            if (existing == null) {
                current + CartItem(product, quantity)
            } else {
                current.map {
                    if (it.product.id == product.id) {
                        it.copy(quantity = it.quantity + quantity)
                    } else {
                        it
                    }
                }
            }
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        cartFlow.update { current ->
            if (quantity <= 0) {
                current.filterNot { it.product.id == productId }
            } else {
                current.map {
                    if (it.product.id == productId) {
                        it.copy(quantity = quantity)
                    } else {
                        it
                    }
                }
            }
        }
    }

    override suspend fun removeFromCart(productId: String) {
        cartFlow.update { current ->
            current.filterNot { it.product.id == productId }
        }
    }

    override suspend fun clearCart() {
        cartFlow.value = emptyList()
    }

    override suspend fun placeOrder(shippingAddress: String): Boolean {
        if (cartFlow.value.isEmpty()) return false
        clearCart()
        return true
    }
}
