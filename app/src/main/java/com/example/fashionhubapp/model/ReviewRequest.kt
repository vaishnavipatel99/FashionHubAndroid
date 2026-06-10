    package com.example.fashionhubapp.model

    data class ReviewRequest(
        val pid: Int,
        val uid: Int,
        val oid: Int,
        val orderItemId: Int,
        val rating: Int,
        val feedback: String,
        val colorSelected: String? = null,
        val sizeSelected: String? = null
    )