package com.example.fashionhubapp.api
import com.example.fashionhubapp.model.Cart
import com.example.fashionhubapp.model.CartRequest
import com.example.fashionhubapp.model.Category
import com.example.fashionhubapp.model.CategoryRequest
import com.example.fashionhubapp.model.LoginRequest
import com.example.fashionhubapp.model.LoginResponse
import com.example.fashionhubapp.model.Product
import com.example.fashionhubapp.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("Auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    @POST("Users")
    fun register(@Body request: RegisterRequest): Call<Any>
    @GET("Categories")
    fun getCategories(): Call<List<Category>>
    @POST("Categories")
    fun createCategory(@Body category: CategoryRequest): Call<Any>
    @PUT("Categories/{id}")
    fun updateCategory(
        @Path("id") id: Int,
        @Body category: CategoryRequest
    ): Call<Any>
    @DELETE("Categories/{id}")
    fun deleteCategory(
        @Path("id") id: Int
    ): Call<Any>

    @GET("Products")
    fun getProducts(): Call<List<Product>>

    @POST("Cart")
    fun addToCart(
        @Body cartRequest: CartRequest
    ): Call<Cart>

    @GET("Cart/user/{uid}")
    fun getCartByUser(
        @Path("uid") uid: Int
    ): Call<List<Cart>>
}