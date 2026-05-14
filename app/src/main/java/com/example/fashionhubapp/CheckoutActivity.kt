package com.example.fashionhubapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CheckoutActivity : AppCompatActivity() {

    private lateinit var edtAddress: EditText
    private lateinit var txtAmount: TextView
    private lateinit var btnPlaceOrder: Button

    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        edtAddress = findViewById(R.id.edtAddress)
        txtAmount = findViewById(R.id.txtAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        totalAmount = intent.getDoubleExtra("TOTAL", 0.0)
        txtAmount.text = "Total: ₹$totalAmount"

        btnPlaceOrder.setOnClickListener {
            val address = edtAddress.text.toString().trim()

            if (address.isEmpty()) {
                toast("Enter delivery address")
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("TOTAL", totalAmount)
            intent.putExtra("ADDRESS", address)
            startActivity(intent)
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}