package com.quickgrocery.network

import com.squareup.moshi.Json


data class ApiCategory(
    val id: Int,
    val name: String
)

data class ApiProduct(
    val id: Int,
    val name: String,
    val description: String?,
    val price: String,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "category_id") val categoryId: Int?,
    @Json(name = "stock_quantity") val stockQuantity: Int?
)

data class ApiCartItem(
    @Json(name = "product_id") val productId: Int,
    val name: String,
    val description: String?,
    val price: String,
    @Json(name = "image_url") val imageUrl: String?,
    val quantity: Int,
    @Json(name = "available_stock") val availableStock: Int?
)

data class ApiOrder(
    val id: Int,
    @Json(name = "order_date") val orderDate: String?,
    @Json(name = "total_amount") val totalAmount: String?,
    val status: String?,
    @Json(name = "shipping_address") val shippingAddress: String?
)

data class ApiAdminOrder(
    val id: Int,
    @Json(name = "order_date") val orderDate: String?,
    @Json(name = "total_amount") val totalAmount: String?,
    val status: String?,
    @Json(name = "shipping_address") val shippingAddress: String?,
    @Json(name = "user_id") val userId: Int?,
    val email: String?,
    val username: String?,
    @Json(name = "item_count") val itemCount: Int?,
    @Json(name = "total_quantity") val totalQuantity: Int?
)

data class ApiOrderItem(
    @Json(name = "product_id") val productId: Int,
    val name: String,
    val quantity: Int,
    val price: String
)

data class ApiAdminOrderDetail(
    val id: Int,
    @Json(name = "order_date") val orderDate: String?,
    @Json(name = "total_amount") val totalAmount: String?,
    val status: String?,
    @Json(name = "shipping_address") val shippingAddress: String?,
    @Json(name = "user_id") val userId: Int?,
    val email: String?,
    val username: String?,
    val items: List<ApiOrderItem>
)

data class ApiInventoryItem(
    val id: Int,
    val name: String,
    val description: String?,
    val price: String,
    @Json(name = "stock_quantity") val stockQuantity: Int?,
    @Json(name = "category_name") val categoryName: String?
)

data class AuthRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @Json(name = "full_name") val fullName: String?,
    val address: String?,
    @Json(name = "phone_number") val phoneNumber: String?
)

data class AuthResponse(
    val message: String?,
    val token: String?
)

data class AddToCartRequest(
    @Json(name = "productId") val productId: Int,
    val quantity: Int
)

data class UpdateCartRequest(
    val quantity: Int
)

data class PlaceOrderRequest(
    @Json(name = "shippingAddress") val shippingAddress: String
)

data class UpdateInventoryRequest(
    @Json(name = "stockQuantity") val stockQuantity: Int? = null,
    val price: String? = null
)
