    package com.example.fashionhubapp.model

    data class  Cart(
        val cartId: Int,
        val uid: Int,
        val pid: Int,
        val sizeSelected: String,
        var quantity: Int,
        val colorSelected: String,
        val addedAt: String?
    )

