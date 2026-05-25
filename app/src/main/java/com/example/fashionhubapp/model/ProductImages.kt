package com.example.fashionhubapp.model

data class ProductImages(
    val gallery: List<String>,

    val colors: Map<String, List<String>>
)