package com.example.fashionhubapp.model

data class Product (
    val pid: Int,
    val vid: Int,
    val cid: Int,
    val productName: String,
    val description: String,
    val price: Double?,
    val discount: Double?,
    val fabric: String,
    val color: String,
    val size: String,
    val stock: Int,
    val productImage: String,
    val createdAt: String,
    val isActive: Boolean?

)