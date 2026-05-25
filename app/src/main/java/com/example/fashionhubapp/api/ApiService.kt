    package com.example.fashionhubapp.api
    import com.example.fashionhubapp.model.Cart
    import com.example.fashionhubapp.model.CartRequest
    import com.example.fashionhubapp.model.Category
    import com.example.fashionhubapp.model.CategoryRequest
    import com.example.fashionhubapp.model.CouponResponse
    import com.example.fashionhubapp.model.LoginRequest
    import com.example.fashionhubapp.model.LoginResponse
    import com.example.fashionhubapp.model.Order
    import com.example.fashionhubapp.model.OrderItemRequest
    import com.example.fashionhubapp.model.OrderItemResponse
    import com.example.fashionhubapp.model.OrderRequest
    import com.example.fashionhubapp.model.OrderResponse
    import com.example.fashionhubapp.model.PaymentRequest
    import com.example.fashionhubapp.model.Product
    import com.example.fashionhubapp.model.RegisterRequest
    import com.example.fashionhubapp.model.UserProfile
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

        // ================= CART =================

        @POST("Carts")
        fun addToCart(
            @Body cartRequest: CartRequest
        ): Call<Cart>

        @GET("Carts")
        fun getAllCart(): Call<List<Cart>>

        @GET("Carts/{id}")
        fun getCartById(
            @Path("id") id: Int
        ): Call<Cart>

        @PUT("Carts/{id}")
        fun updateCart(
            @Path("id") id: Int,
            @Body request: CartRequest
        ): Call<Any>

        @DELETE("Carts/{id}")
        fun deleteCart(
            @Path("id") id: Int
        ): Call<Any>
        // GET USER PROFILE

        @GET("Users/{id}")
        fun getUserById(
            @Path("id") id: Int
        ): Call<UserProfile>

        // UPDATE USER PROFILE

        @PUT("Users/{id}")
        fun updateUser(
            @Path("id") id: Int,
            @Body user: UserProfile
        ): Call<Any>
        // ================= ORDERS =================
        @POST("Orders")
        fun createOrder(
            @Body order: OrderRequest
        ): Call<OrderResponse>

        // ================= ORDER ITEMS =================
        @POST("OrderItems")
        fun createOrderItem(
            @Body item: OrderItemRequest
        ): Call<Any>

        // ================= PAYMENTS =================
        @POST("Payments")
        fun createPayment(
            @Body payment: PaymentRequest
        ): Call<Any>
        @GET("Orders")
        fun getOrders(): Call<List<Order>>

        @GET("Coupons")
        fun getCoupons():
                Call<List<CouponResponse>>
        @GET("OrderItems/order/{oid}")
        fun getOrderItems(
            @Path("oid") oid: Int
        ): Call<List<OrderItemResponse>>
    }