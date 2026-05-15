package com.example.fashionhubapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var spinnerPayment: Spinner
    private lateinit var btnPay: Button
    private lateinit var txtAmount: TextView

    private var totalAmount = 0.0
    private var address = ""

    private val api = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        spinnerPayment = findViewById(R.id.spinnerPayment)
        btnPay = findViewById(R.id.btnPay)
        txtAmount = findViewById(R.id.txtAmount)

        address = intent.getStringExtra("ADDRESS") ?: ""
        totalAmount = intent.getDoubleExtra("TOTAL", 0.0)

        txtAmount.text = "Pay ₹$totalAmount"

        spinnerPayment.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
//            listOf("Select", "CASH", "CARD", "WALLET")
            listOf("Select", "COD", "CARD", "UPI", "WALLET")
        )

        btnPay.setOnClickListener {
            val method = spinnerPayment.selectedItem.toString()
            if (method == "Select") {
                toast("Select payment method")
                return@setOnClickListener
            }
            validateCartAndProceed(method)
        }
    }

    // ================= STEP 1: VALIDATE CART =================
    private fun validateCartAndProceed(method: String) {

        val uid = getSharedPreferences("FashionHub", MODE_PRIVATE)
            .getInt("uid", -1)

        if (uid == -1) {
            toast("User not logged in")
            return
        }

        api.getAllCart().enqueue(object : Callback<List<Cart>> {
            override fun onResponse(
                call: Call<List<Cart>>,
                response: Response<List<Cart>>
            ) {
                val cartList = response.body()?.filter { it.uid == uid } ?: emptyList()

                if (cartList.isEmpty()) {
                    toast("Cart is empty 🛒")
                    return
                }

                createOrder(cartList, method)
            }

            override fun onFailure(call: Call<List<Cart>>, t: Throwable) {
                toast("Failed to load cart")
            }
        })
    }

    // ================= STEP 2: CREATE ORDER =================
    private fun createOrder(cartList: List<Cart>, method: String) {

        val pref = getSharedPreferences("FashionHub", MODE_PRIVATE)
        val uid = pref.getInt("uid", 0)
        val username = pref.getString("name", "User") ?: "User"

        val orderReq = OrderRequest(
            uid = uid,
            amount = totalAmount,
            orderStatus = "PENDING",
            paymentStatus = "PENDING",
            deliveryAddress = address,
            username = username
        )

        api.createOrder(orderReq).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val oid = response.body()!!.orderId

                    println("ORDER CREATED ID = $oid")

                    createOrderItems(oid, cartList, method)
                } else {
                    toast("Order creation failed")
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                toast("Order failed")
            }
        })
    }

    // ================= STEP 3: CREATE ORDER ITEMS =================
    private fun createOrderItems(
        oid: Int,
        cartList: List<Cart>,
        method: String
    ) {
        var completed = 0

        cartList.forEach { item ->
            val req = OrderItemRequest(
                oid = oid,
                pid = item.pid,
                quantity = item.quantity,
                sizeSelected = item.sizeSelected,
                colorSelected = item.colorSelected,
                price = 0.0
            )

            api.createOrderItem(req).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    completed++
                    if (completed == cartList.size) {
                        createPayment(oid, method)
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    toast("Order item failed")
                }
            })
        }
    }

    // ================= STEP 4: PAYMENT =================
//    private fun createPayment(oid: Int, method: String) {
//
//        val paymentReq = PaymentRequest(
//            oid = oid,
//            method = method,
//            amount = totalAmount,
//            payStatus = "Paid"
//        )
//
//        api.createPayment(paymentReq).enqueue(object : Callback<Any> {
//            override fun onResponse(call: Call<Any>, response: Response<Any>) {
//
//                if (!response.isSuccessful) {
//                    toast("Payment failed ❌")
//                    return
//                }
//
//                toast("Order placed successfully 🎉")
//                clearCart()
//                finish()   // ✅ NOT finishAffinity()
//            }
//
//            override fun onFailure(call: Call<Any>, t: Throwable) {
//                toast("Payment error ❌")
//            }
//        })
//    }

    private fun createPayment(oid: Int, method: String) {

        val status =
            if (method == "COD") "Pending"
            else "Paid"

        val currentDate = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val paymentReq = PaymentRequest(
            oid = oid,
            method = method,
            amount = totalAmount,
            payStatus = status,
            payDate = currentDate

        )

        println("PAYMENT OID = $oid")
        api.createPayment(paymentReq).enqueue(object : Callback<Any> {

//            override fun onResponse(call: Call<Any>, response: Response<Any>) {
//
//                if (!response.isSuccessful) {
//                    toast("Payment failed ❌")
//                    return
//                }
//
//                toast("Order placed successfully 🎉")
//
//                clearCart()
//
//                finish()
//            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {

                if (!response.isSuccessful) {

                    val errorBody = response.errorBody()?.string()

                    println("PAYMENT ERROR CODE = ${response.code()}")
                    println("PAYMENT ERROR BODY = $errorBody")

                    toast("Payment failed ❌ ${response.code()}")

                    return
                }

                println("PAYMENT SUCCESS")

                toast("Order placed successfully 🎉")

                clearCart()

                finish()
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                println("PAYMENT FAILURE = ${t.message}")

                toast("Payment error ❌")
            }
        })
    }
    // ================= CLEAR CART (ONLY AFTER SUCCESS) =================
    private fun clearCart() {

        val uid = getSharedPreferences("FashionHub", MODE_PRIVATE)
            .getInt("uid", 0)

        api.getAllCart().enqueue(object : Callback<List<Cart>> {
            override fun onResponse(
                call: Call<List<Cart>>,
                response: Response<List<Cart>>
            ) {
                response.body()
                    ?.filter { it.uid == uid }
                    ?.forEach {
                        api.deleteCart(it.cartId).enqueue(object : Callback<Any> {
                            override fun onResponse(call: Call<Any>, response: Response<Any>) {}
                            override fun onFailure(call: Call<Any>, t: Throwable) {}
                        })
                    }
            }

            override fun onFailure(call: Call<List<Cart>>, t: Throwable) {}
        })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}