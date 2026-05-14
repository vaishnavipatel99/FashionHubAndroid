package com.example.fashionhubapp.model

data class UserProfile (
    val uid: Int,
    val gid: Int,

    var username: String,
    var email: String,
    var password: String,
    var mobileno: String,
    var city: String,
    var gender: String,
    var dob: String,
    var state: String
)