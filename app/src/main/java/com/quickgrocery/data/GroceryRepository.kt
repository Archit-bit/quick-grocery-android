package com.quickgrocery.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GroceryRepository {
    val products: Flow<List<Product>>
    val cart: StateFlow<List<CartItem>>

    suspend fun getProduct(productId: String): Product?
    suspend fun addToCart(product: Product, quantity: Int)
    suspend fun updateQuantity(productId: String, quantity: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()
    suspend fun placeOrder(shippingAddress: String): Boolean
}
