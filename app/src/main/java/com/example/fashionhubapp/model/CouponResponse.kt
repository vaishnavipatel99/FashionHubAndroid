package com.example.fashionhubapp.model

data class CouponResponse(

    val couponId: Int,

    val couponCode: String,

    val discountType: String,

    val minimumOrderAmount: Double,

    val maximumOrderAmount: Double
)