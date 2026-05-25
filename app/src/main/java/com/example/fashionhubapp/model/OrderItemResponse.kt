package com.example.fashionhubapp.model

data class OrderItemResponse(
    val orderItemId: Int,
    val oid: Int,
    val productId: Int,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val sizeSelected: String,
    val colorSelected: String,
    val price: Double
)