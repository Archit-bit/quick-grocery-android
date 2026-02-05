package com.quickgrocery.network

import com.quickgrocery.data.CartItem
import com.quickgrocery.data.GroceryRepository
import com.quickgrocery.data.Product
import com.quickgrocery.data.sampleProducts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkGroceryRepository @Inject constructor(
    private val api: ApiService,
    private val authStore: AuthStore
) : GroceryRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val productsFlow = MutableStateFlow(sampleProducts())
    private val cartFlow = MutableStateFlow<List<CartItem>>(emptyList())
    private var categoryMap: Map<Int, String> = emptyMap()

    override val products = productsFlow.asStateFlow()
    override val cart: StateFlow<List<CartItem>> = cartFlow.asStateFlow()

    init {
        refreshProducts()
        refreshCart()
    }

    override suspend fun getProduct(productId: String): Product? {
        return productsFlow.value.firstOrNull { it.id == productId }
            ?: fetchProduct(productId)
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        if (quantity <= 0) return
        val token = authStore.token.value
        if (token.isNullOrBlank()) {
            addToLocalCart(product, quantity)
            return
        }
        runCatching {
            api.addToCart(AddToCartRequest(product.id.toInt(), quantity))
            refreshCart()
        }.onFailure {
            addToLocalCart(product, quantity)
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val token = authStore.token.value
        if (token.isNullOrBlank()) {
            updateLocalQuantity(productId, quantity)
            return
        }
        runCatching {
            if (quantity <= 0) {
                api.removeCartItem(productId.toInt())
            } else {
                api.updateCart(productId.toInt(), UpdateCartRequest(quantity))
            }
            refreshCart()
        }.onFailure {
            updateLocalQuantity(productId, quantity)
        }
    }

    override suspend fun removeFromCart(productId: String) {
        val token = authStore.token.value
        if (token.isNullOrBlank()) {
            updateLocalQuantity(productId, 0)
            return
        }
        runCatching {
            api.removeCartItem(productId.toInt())
            refreshCart()
        }.onFailure {
            updateLocalQuantity(productId, 0)
        }
    }

    override suspend fun clearCart() {
        val token = authStore.token.value
        if (token.isNullOrBlank()) {
            cartFlow.value = emptyList()
            return
        }
        val current = cartFlow.value
        current.forEach { item ->
            runCatching { api.removeCartItem(item.product.id.toInt()) }
        }
        refreshCart()
    }

    override suspend fun placeOrder(shippingAddress: String): Boolean {
        val token = authStore.token.value
        android.util.Log.d("NetworkGroceryRepo", "placeOrder called, token present: ${!token.isNullOrBlank()}")
        if (token.isNullOrBlank()) {
            android.util.Log.w("NetworkGroceryRepo", "No token, clearing cart locally")
            clearCart()
            return true
        }
        return runCatching {
            android.util.Log.d("NetworkGroceryRepo", "Calling API placeOrder with address: $shippingAddress")
            api.placeOrder(PlaceOrderRequest(shippingAddress))
            android.util.Log.d("NetworkGroceryRepo", "Order placed successfully!")
            refreshCart()
            true
        }.getOrElse { error ->
            android.util.Log.e("NetworkGroceryRepo", "Order failed: ${error.message}", error)
            false
        }
    }

    private fun refreshProducts() {
        scope.launch {
            val categories = runCatching { api.getCategories() }.getOrNull()
            categoryMap = categories?.associate { it.id to it.name }.orEmpty()

            val products = runCatching { api.getProducts() }.getOrNull()
            if (!products.isNullOrEmpty()) {
                productsFlow.value = products.map { it.toDomain(categoryMap) }
            }
        }
    }

    private fun refreshCart() {
        scope.launch {
            val token = authStore.token.value
            if (token.isNullOrBlank()) return@launch
            val cartItems = runCatching { api.getCart() }.getOrNull()
            if (cartItems != null) {
                val productLookup = productsFlow.value.associateBy { it.id }
                cartFlow.value = cartItems.map { it.toDomain(productLookup) }
            }
        }
    }

    private suspend fun fetchProduct(productId: String): Product? {
        return runCatching {
            val apiProduct = api.getProduct(productId.toInt())
            apiProduct.toDomain(categoryMap)
        }.getOrNull()
    }

    private fun addToLocalCart(product: Product, quantity: Int) {
        val current = cartFlow.value.toMutableList()
        val existing = current.firstOrNull { it.product.id == product.id }
        if (existing == null) {
            current.add(CartItem(product, quantity))
        } else {
            val index = current.indexOf(existing)
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        }
        cartFlow.value = current
    }

    private fun updateLocalQuantity(productId: String, quantity: Int) {
        val current = cartFlow.value.toMutableList()
        val existing = current.firstOrNull { it.product.id == productId } ?: return
        if (quantity <= 0) {
            current.remove(existing)
        } else {
            val index = current.indexOf(existing)
            current[index] = existing.copy(quantity = quantity)
        }
        cartFlow.value = current
    }
}

private fun ApiProduct.toDomain(categoryMap: Map<Int, String>): Product {
    val categoryName = categoryId?.let { categoryMap[it] } ?: "Other"
    return Product(
        id = id.toString(),
        name = name,
        category = categoryName,
        price = price.toDoubleOrNull() ?: 0.0,
        unit = "1 pc",
        description = description ?: ""
    )
}

private fun ApiCartItem.toDomain(products: Map<String, Product>): CartItem {
    val existing = products[productId.toString()]
    val product = existing ?: Product(
        id = productId.toString(),
        name = name,
        category = "Other",
        price = price.toDoubleOrNull() ?: 0.0,
        unit = "1 pc",
        description = description ?: ""
    )
    return CartItem(product = product, quantity = quantity)
}
