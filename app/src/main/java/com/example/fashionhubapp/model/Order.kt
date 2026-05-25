package com.example.fashionhubapp.model

data class Order (
    val oid: Int,

    val orderDate: String,

    val amount: Double,

    val orderStatus: String,

    val paymentStatus: String,

    val deliveryAddress: String,

    val estimatedDelivery: String,

    val username: String
)