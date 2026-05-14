package com.example.fashionhubapp.model

data class OrderItemRequest (
    val oid: Int,
    val pid: Int,
    val quantity: Int,
    val sizeSelected: String,
    val colorSelected: String,
    val price: Double

)