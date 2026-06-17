package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.adapters.CouponAdapter
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.CouponResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutActivity : AppCompatActivity() {

    private lateinit var edtAddress: EditText
    private lateinit var txtAmount: TextView
    private lateinit var btnPlaceOrder: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var edtCoupon: EditText
    private lateinit var btnApplyCoupon: Button
    private lateinit var txtDiscount: TextView
    private lateinit var recyclerCoupons: RecyclerView
    private lateinit var couponAdapter: CouponAdapter

    private var couponList = ArrayList<CouponResponse>()

    private var totalAmount = 0.0
    private var discountAmount = 0.0
    private var finalAmount = 0.0

    private val api = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        window.statusBarColor = getColor(android.R.color.white)
        window.navigationBarColor = getColor(android.R.color.white)

        edtAddress = findViewById(R.id.edtAddress)
        txtAmount = findViewById(R.id.txtAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        edtCoupon = findViewById(R.id.edtCoupon)
        btnApplyCoupon = findViewById(R.id.btnApplyCoupon)
        txtDiscount = findViewById(R.id.txtDiscount)
        recyclerCoupons = findViewById(R.id.recyclerCoupons)

//        val btnBack = findViewById<ImageView>(R.id.btnBack)

//        btnBack.setOnClickListener { finish() }

        totalAmount = intent.getDoubleExtra("TOTAL", 0.0)
        finalAmount = totalAmount
        txtAmount.text = "₹ $finalAmount"

        loadCoupons()
        setupBottomNavigation()


        btnApplyCoupon.setOnClickListener {

            val code = edtCoupon.text.toString().trim()

            if (code.isEmpty()) {
                Toast.makeText(this, "Enter coupon code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val found = couponList.find {
                it.couponCode.equals(code, true)
            }

            if (found != null) {
                applyCoupon(found)
            } else {
                Toast.makeText(this, "Invalid Coupon", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlaceOrder.setOnClickListener {

            val address = edtAddress.text.toString().trim()

            if (address.isEmpty()) {
                Toast.makeText(this, "Enter delivery address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("TOTAL", finalAmount)
            intent.putExtra("ADDRESS", address)
            startActivity(intent)
        }
    }
    private fun setupBottomNavigation() {

        bottomNavigation.selectedItemId = R.id.nav_cart

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    startActivity(
                        Intent(this, UserDashboardActivity::class.java)
                    )
                    true
                }

                R.id.nav_cart -> {
                    startActivity(
                        Intent(this, CartActivity::class.java)
                    )
                    true
                }

                R.id.nav_profile -> {
                    startActivity(
                        Intent(this, ProfileActivity::class.java)
                    )
                    true
                }

                R.id.nav_orders -> {
                    startActivity(
                        Intent(this, OrdersActivity::class.java)
                    )
                    true
                }

                else -> false
            }
        }
    }
    // ✅ ONLY ONE LOAD FUNCTION
    private fun loadCoupons() {

        api.getCoupons()
            .enqueue(object : Callback<List<CouponResponse>> {

                override fun onResponse(
                    call: Call<List<CouponResponse>>,
                    response: Response<List<CouponResponse>>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        couponList = ArrayList(response.body()!!)

                        recyclerCoupons.layoutManager =
                            LinearLayoutManager(this@CheckoutActivity)

                        couponAdapter =
                            CouponAdapter(
                                this@CheckoutActivity,
                                couponList
                            ) { coupon ->
                                applyCoupon(coupon)
                            }

                        recyclerCoupons.adapter = couponAdapter

                        Toast.makeText(
                            this@CheckoutActivity,
                            "Coupons Loaded: ${couponList.size}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@CheckoutActivity,
                            "No Coupons Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<CouponResponse>>, t: Throwable) {
                    Toast.makeText(this@CheckoutActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun applyCoupon(item: CouponResponse) {

        if (totalAmount < item.minimumOrderAmount) {

            Toast.makeText(
                this,
                "Minimum order ₹${item.minimumOrderAmount}",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        edtCoupon.setText(item.couponCode)

        discountAmount =
            item.discountType.toDoubleOrNull() ?: 0.0

        finalAmount = totalAmount - discountAmount

        if (finalAmount < 0) {
            finalAmount = 0.0
        }

        txtDiscount.text =
            "Discount : ₹%.2f".format(discountAmount)

        txtAmount.text =
            "₹ %.2f".format(finalAmount)

        Toast.makeText(
            this,
            "Coupon Applied Successfully",
            Toast.LENGTH_SHORT
        ).show()
    }



}