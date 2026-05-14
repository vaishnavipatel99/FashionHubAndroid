package com.example.fashionhubapp.model

data class OrderRequest (
    val uid: Int,
    val amount: Double,
    val orderStatus: String,
    val paymentStatus: String,
    val deliveryAddress: String,
    val username: String
)