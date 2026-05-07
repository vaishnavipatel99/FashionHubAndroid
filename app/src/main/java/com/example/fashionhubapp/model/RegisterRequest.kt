package com.example.fashionhubapp.model

data class RegisterRequest (
    val username: String,
    val email: String,
    val password: String,
    val gid: Int,
    val gender: String,
    val state: String,
    val city: String

)