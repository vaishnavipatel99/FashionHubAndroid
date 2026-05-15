    package com.example.fashionhubapp.model

    data class PaymentRequest (
        val oid: Int,
        val method: String,
        val amount: Double,
        val payStatus: String,
        val payDate: String

    )