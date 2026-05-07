package com.example.fashionhubapp.model

data class LoginResponse(
    val token: String,
    val role: String?,
    val user: User?
)

data class User(
    val uid: Int,
    val gid: Int,
    val username: String
)