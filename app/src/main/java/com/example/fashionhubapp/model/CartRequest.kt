    package com.example.fashionhubapp.model

    data class CartRequest (
        val uid: Int,
        val pid: Int,
        val sizeSelected: String,
        val quantity: Int,
        val colorSelected: String,
        val selectedImage: String

    )
