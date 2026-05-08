package com.example.fashionhubapp.model

data class Cart (
    val cartId: Int,
    val uid: Int,
    val pid: Int,
    val sizeSelected: String,
    val quantity: String,
    val addedAt: String,
    val colorSelected: String
)