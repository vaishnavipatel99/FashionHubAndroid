package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionhubapp.R
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class PaymentActivity : AppCompatActivity() {

    private lateinit var spinnerPayment: Spinner
    private lateinit var spinnerWallet: Spinner

    private lateinit var btnPay: Button
    private lateinit var txtAmount: TextView

    private lateinit var layoutCard: LinearLayout
    private lateinit var layoutUpi: LinearLayout
    private lateinit var layoutWallet: LinearLayout

    private lateinit var edtCardNumber: EditText
    private lateinit var edtExpiry: EditText
    private lateinit var edtCvv: EditText
    private lateinit var edtUpi: EditText

    private var totalAmount = 0.0
    private var address = ""

    private val api = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Checkout.preload()

        setContentView(R.layout.activity_payment)

        spinnerPayment = findViewById(R.id.spinnerPayment)
        spinnerWallet = findViewById(R.id.spinnerWallet)

        btnPay = findViewById(R.id.btnPay)
        txtAmount = findViewById(R.id.txtAmount)

        layoutCard = findViewById(R.id.layoutCard)
        layoutUpi = findViewById(R.id.layoutUpi)
        layoutWallet = findViewById(R.id.layoutWallet)

        edtCardNumber = findViewById(R.id.edtCardNumber)
        edtExpiry = findViewById(R.id.edtExpiry)
        edtCvv = findViewById(R.id.edtCvv)
        edtUpi = findViewById(R.id.edtUpi)

        address = intent.getStringExtra("ADDRESS") ?: ""
        totalAmount = intent.getDoubleExtra("TOTAL", 0.0)

        txtAmount.text = "₹%.2f".format(totalAmount)

        setupBottomNavigation()
        setupPaymentSpinner()
        setupWalletSpinner()

        btnPay.setOnClickListener {

            val method = spinnerPayment.selectedItem.toString()

            when (method) {

                "UPI" -> {

                    if (edtUpi.text.toString().trim().isEmpty()) {
                        toast("Enter UPI ID")
                        return@setOnClickListener
                    }

                    validateCartAndProceed("UPI")
                }

                "Wallet" -> {

                    validateCartAndProceed(
                        spinnerWallet.selectedItem.toString()
                    )
                }

                "Debit / Credit Card" -> {

                    if (edtCardNumber.text.toString().trim().isEmpty() ||
                        edtExpiry.text.toString().trim().isEmpty() ||
                        edtCvv.text.toString().trim().isEmpty()
                    ) {

                        toast("Enter Card Details")
                        return@setOnClickListener
                    }

                    validateCartAndProceed("Card")
                }

                "Cash On Delivery" -> {

                    validateCartAndProceed("Cash On Delivery")
                }
            }
        }    }

    private fun setupBottomNavigation() {

        val bottomNavigation =
            findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_cart

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    startActivity(
                        Intent(
                            this,
                            UserDashboardActivity::class.java
                        )
                    )
                    true
                }

                R.id.nav_cart -> true

                R.id.nav_profile -> {
                    startActivity(
                        Intent(
                            this,
                            ProfileActivity::class.java
                        )
                    )
                    true
                }

                R.id.nav_orders -> {
                    startActivity(
                        Intent(
                            this,
                            OrdersActivity::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }
    }

    private fun setupPaymentSpinner() {

        val methods = arrayListOf(
            "Cash On Delivery",
            "UPI",
            "Wallet",
            "Debit / Credit Card"
        )

        spinnerPayment.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                methods
            )

        spinnerPayment.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    layoutCard.visibility = View.GONE
                    layoutUpi.visibility = View.GONE
                    layoutWallet.visibility = View.GONE

                    when (position) {

                        1 -> layoutUpi.visibility = View.VISIBLE

                        2 -> layoutWallet.visibility = View.VISIBLE

                        3 -> layoutCard.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupWalletSpinner() {

        val wallets = arrayListOf(
            "Paytm",
            "PhonePe",
            "Google Pay",
            "Amazon Pay"
        )

        spinnerWallet.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                wallets
            )
    }


    private fun validateCartAndProceed(
        method: String
    ) {

        val uid =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            ).getInt("uid", -1)

        if (uid == -1) {

            toast("User not logged in")
            return
        }

        api.getAllCart()
            .enqueue(object : Callback<List<Cart>> {

                override fun onResponse(
                    call: Call<List<Cart>>,
                    response: Response<List<Cart>>
                ) {

                    val cartList =
                        response.body()
                            ?.filter { it.uid == uid }
                            ?: emptyList()

                    if (cartList.isEmpty()) {

                        toast("Cart is empty")
                        return
                    }

                    createOrder(cartList, method)
                }

                override fun onFailure(
                    call: Call<List<Cart>>,
                    t: Throwable
                ) {

                    toast("Failed to load cart")
                }
            })
    }

    private fun createOrder(
        cartList: List<Cart>,
        method: String
    ) {

        val pref =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            )

        val orderRequest =
            OrderRequest(
                uid = pref.getInt("uid", 0),
                amount = totalAmount,
                orderStatus = "Pending",
                paymentStatus =
                    if (method == "Cash On Delivery")
                        "Pending"
                    else
                        "Paid",
                deliveryAddress = address,
                username = pref.getString("name", "User") ?: "User"
            )

        api.createOrder(orderRequest)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {

                    if (response.isSuccessful &&
                        response.body() != null
                    ) {

                        createOrderItems(
                            response.body()!!.orderId,
                            cartList,
                            method
                        )
                    }
                }

                override fun onFailure(
                    call: Call<OrderResponse>,
                    t: Throwable
                ) {

                    toast("Order Failed")
                }
            })
    }

    private fun createOrderItems(
        oid: Int,
        cartList: List<Cart>,
        method: String
    ) {

        var completed = 0

        cartList.forEach { item ->

            val req =
                OrderItemRequest(
                    oid = oid,
                    pid = item.pid,
                    quantity = item.quantity,
                    sizeSelected = item.sizeSelected,
                    colorSelected = item.colorSelected,
                    price = 0.0
                )

            api.createOrderItem(req)
                .enqueue(object : Callback<Any> {

                    override fun onResponse(
                        call: Call<Any>,
                        response: Response<Any>
                    ) {

                        completed++

                        if (completed == cartList.size) {

                            createPayment(oid, method)
                        }
                    }

                    override fun onFailure(
                        call: Call<Any>,
                        t: Throwable
                    ) {

                        toast("Order Item Failed")
                    }
                })
        }
    }

    private fun createPayment(
        oid: Int,
        method: String
    ) {

        val paymentRequest =
            PaymentRequest(
                oid = oid,
                method = method,
                amount = totalAmount,
                payStatus = if (method == "Cash On Delivery") "Pending" else "Paid",
                payDate = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            )

        api.createPayment(paymentRequest)
            .enqueue(object : Callback<Any> {

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {

                    clearCart()

                    toast("Payment Successfully")

                    Handler(Looper.getMainLooper())
                        .postDelayed({

                            startActivity(
                                Intent(
                                    this@PaymentActivity,
                                    OrdersActivity::class.java
                                )
                            )

                            finishAffinity()

                        }, 1500)
                }

                override fun onFailure(
                    call: Call<Any>,
                    t: Throwable
                ) {

                    toast("Payment Save Failed")
                }
            })
    }

    private fun clearCart() {

        val uid =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            ).getInt("uid", 0)

        api.getAllCart()
            .enqueue(object : Callback<List<Cart>> {

                override fun onResponse(
                    call: Call<List<Cart>>,
                    response: Response<List<Cart>>
                ) {

                    response.body()
                        ?.filter { it.uid == uid }
                        ?.forEach {

                            api.deleteCart(it.cartId)
                                .enqueue(object : Callback<Any> {

                                    override fun onResponse(
                                        call: Call<Any>,
                                        response: Response<Any>
                                    ) {
                                    }

                                    override fun onFailure(
                                        call: Call<Any>,
                                        t: Throwable
                                    ) {
                                    }
                                })
                        }
                }

                override fun onFailure(
                    call: Call<List<Cart>>,
                    t: Throwable
                ) {
                }
            })
    }

    private fun toast(msg: String) {

        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}