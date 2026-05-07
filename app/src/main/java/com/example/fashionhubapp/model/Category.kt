package com.example.fashionhubapp.model

data class Category(
    val cid: Int,
    val categoryName: String,
    val occasion: String,
    val season: String,
    val isActive: Boolean
)