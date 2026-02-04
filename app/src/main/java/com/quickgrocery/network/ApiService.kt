package com.quickgrocery.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/products")
    suspend fun getProducts(): List<ApiProduct>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): ApiProduct

    @GET("api/categories")
    suspend fun getCategories(): List<ApiCategory>

    @GET("api/categories/{id}")
    suspend fun getCategory(@Path("id") id: Int): ApiCategory

    @GET("api/cart")
    suspend fun getCart(): List<ApiCartItem>

    @POST("api/cart")
    suspend fun addToCart(@Body body: AddToCartRequest)

    @PUT("api/cart/{productId}")
    suspend fun updateCart(@Path("productId") productId: Int, @Body body: UpdateCartRequest)

    @DELETE("api/cart/{productId}")
    suspend fun removeCartItem(@Path("productId") productId: Int)

    @POST("api/orders")
    suspend fun placeOrder(@Body body: PlaceOrderRequest)

    @GET("api/orders")
    suspend fun getOrders(): List<ApiOrder>

    @GET("api/orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: Int): ApiOrder

    @POST("api/auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse
}
