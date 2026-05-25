package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import java.util.Date
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var spinnerPayment: Spinner
    private lateinit var btnPay: Button
    private lateinit var txtAmount: TextView

    private lateinit var layoutCard: LinearLayout
    private lateinit var edtCardNumber: EditText
    private lateinit var edtExpiry: EditText
    private lateinit var edtCvv: EditText

    private lateinit var layoutUpi: LinearLayout
    private lateinit var edtUpi: EditText

    private var totalAmount = 0.0
    private var address = ""

    private val api = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_payment)

        // BACK BUTTON

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {

            finish()
        }

        // BOTTOM NAVIGATION

        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottomNavigation
            )

        bottomNavigation.selectedItemId =
            R.id.nav_cart

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

                R.id.nav_cart -> {

                    true
                }

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

        spinnerPayment =
            findViewById(R.id.spinnerPayment)

        btnPay =
            findViewById(R.id.btnPay)

        txtAmount =
            findViewById(R.id.txtAmount)

        layoutCard =
            findViewById(R.id.layoutCard)

        edtCardNumber =
            findViewById(R.id.edtCardNumber)

        edtExpiry =
            findViewById(R.id.edtExpiry)

        edtCvv =
            findViewById(R.id.edtCvv)

        layoutUpi =
            findViewById(R.id.layoutUpi)

        edtUpi =
            findViewById(R.id.edtUpi)

        address =
            intent.getStringExtra("ADDRESS") ?: ""

        totalAmount =
            intent.getDoubleExtra("TOTAL", 0.0)

        txtAmount.text =
            "Pay ₹$totalAmount"

        val methods =
            listOf(
                "Select",
                "COD",
                "CARD",
                "UPI",
                "WALLET"
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

                    val method =
                        methods[position]

                    layoutCard.visibility =
                        if (method == "CARD")
                            View.VISIBLE
                        else
                            View.GONE

                    layoutUpi.visibility =
                        if (method == "UPI")
                            View.VISIBLE
                        else
                            View.GONE
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        btnPay.setOnClickListener {

            val method =
                spinnerPayment.selectedItem.toString()

            if (method == "Select") {

                toast("Select payment method")

                return@setOnClickListener
            }

            if (method == "CARD") {

                if (
                    edtCardNumber.text.isEmpty() ||
                    edtExpiry.text.isEmpty() ||
                    edtCvv.text.isEmpty()
                ) {

                    toast("Enter card details")

                    return@setOnClickListener
                }
            }

            if (method == "UPI") {

                if (edtUpi.text.isEmpty()) {

                    toast("Enter UPI ID")

                    return@setOnClickListener
                }
            }

            validateCartAndProceed(method)
        }
    }

    // VALIDATE CART

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

    // CREATE ORDER

    private fun createOrder(
        cartList: List<Cart>,
        method: String
    ) {

        val pref =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            )

        val uid =
            pref.getInt("uid", 0)

        val username =
            pref.getString("name", "User")
                ?: "User"

        val orderReq =
            OrderRequest(
                uid = uid,
                amount = totalAmount,
                orderStatus = "Pending",
                paymentStatus = "Pending",
                deliveryAddress = address,
                username = username
            )

        api.createOrder(orderReq)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        val oid =
                            response.body()!!.orderId

                        createOrderItems(
                            oid,
                            cartList,
                            method
                        )

                    } else {

                        toast("Order creation failed")
                    }
                }

                override fun onFailure(
                    call: Call<OrderResponse>,
                    t: Throwable
                ) {

                    toast("Order failed")
                }
            })
    }

    // CREATE ORDER ITEMS

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

                            createPayment(
                                oid,
                                method
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<Any>,
                        t: Throwable
                    ) {

                        toast("Order item failed")
                    }
                })
        }
    }

    // PAYMENT

    private fun createPayment(
        oid: Int,
        method: String
    ) {

        val status =
            if (method == "COD")
                "Pending"
            else
                "Paid"

        val currentDate =
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault()
            ).format(Date())

        val paymentReq =
            PaymentRequest(
                oid = oid,
                method = method,
                amount = totalAmount,
                payStatus = status,
                payDate = currentDate
            )

        api.createPayment(paymentReq)
            .enqueue(object : Callback<Any> {

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {

                    if (!response.isSuccessful) {

                        toast("Payment failed")

                        return
                    }

                    clearCart()

                    Toast.makeText(
                        this@PaymentActivity,
                        "Payment Successful 🎉",
                        Toast.LENGTH_LONG
                    ).show()

                    Handler(mainLooper)
                        .postDelayed({

                            val intent =
                                Intent(
                                    this@PaymentActivity,
                                    UserDashboardActivity::class.java
                                )

                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)

                            finish()

                        }, 2000)
                }

                override fun onFailure(
                    call: Call<Any>,
                    t: Throwable
                ) {

                    toast("Payment error")
                }
            })
    }

    // CLEAR CART

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