package com.example.fashionhubapp.model

import com.google.gson.annotations.SerializedName

data class ReviewResponse(

    @SerializedName("rid")
    val rid: Int,

    @SerializedName("pid")
    val pid: Int,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("feedback")
    val feedback: String,

    @SerializedName("createdAt")
    val createdAt: String
)