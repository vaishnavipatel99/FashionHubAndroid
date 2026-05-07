package com.example.fashionhubapp.model

data class CategoryRequest (
    val cid: Int,
    val categoryName: String,
    val occasion: String,
    val season: String,
    val isActive: Boolean
)