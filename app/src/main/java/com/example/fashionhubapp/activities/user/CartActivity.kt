package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.adapters.CartAdapter
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Cart
import com.example.fashionhubapp.model.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity(),
    CartAdapter.CartListener {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CartAdapter

    private lateinit var txtGrandTotal: TextView
    private lateinit var txtEmpty: TextView
    private lateinit var btnCheckout: Button

    private lateinit var bottomNavigation: BottomNavigationView

    private var list = mutableListOf<Cart>()

    private var productMap = HashMap<Int, Product>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cart)

        // =========================
        // FIND VIEW
        // =========================

        recycler = findViewById(R.id.cartRecycler)

        txtGrandTotal = findViewById(R.id.txtGrandTotal)

        txtEmpty = findViewById(R.id.txtEmpty)

        btnCheckout = findViewById(R.id.btnCheckout)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        // =========================
        // BACK BUTTON
        // =========================

        btnBack.setOnClickListener {
            finish()
        }

        // =========================
        // RECYCLER VIEW
        // =========================

        recycler.layoutManager =
            LinearLayoutManager(this)

        adapter =
            CartAdapter(
                list,
                productMap,
                this,
                this
            )

        recycler.adapter = adapter

        // =========================
        // BOTTOM NAVIGATION
        // =========================

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

                R.id.nav_cart -> true

                R.id.nav_orders -> {

                    startActivity(
                        Intent(
                            this,
                            OrdersActivity::class.java
                        )
                    )

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

                R.id.nav_logout -> {

                    val pref =
                        getSharedPreferences(
                            "FashionHub",
                            MODE_PRIVATE
                        )

                    pref.edit().clear().apply()

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                    finish()

                    true
                }

                else -> false
            }
        }

        // =========================
        // LOAD PRODUCTS
        // =========================

        loadProducts()

        // =========================
        // CHECKOUT BUTTON
        // =========================

        btnCheckout.setOnClickListener {

            if (list.isEmpty()) {

                Toast.makeText(
                    this,
                    "Cart is empty",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val totalText =
                txtGrandTotal.text
                    .toString()
                    .replace("₹", "")
                    .trim()

            val total =
                totalText.toDoubleOrNull() ?: 0.0

            val intent =
                Intent(
                    this,
                    CheckoutActivity::class.java
                )

            intent.putExtra(
                "TOTAL",
                total
            )

            startActivity(intent)
        }
    }

    // =========================
    // LOAD PRODUCTS
    // =========================

    private fun loadProducts() {

        RetrofitClient.instance
            .getProducts()
            .enqueue(object : Callback<List<Product>> {

                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        productMap.clear()

                        for (product in response.body()!!) {

                            productMap[product.pid] =
                                product
                        }

                        loadCart()
                    }
                    else {

                        Toast.makeText(
                            this@CartActivity,
                            "Failed to load products",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Product>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CartActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // LOAD CART
    // =========================

    private fun loadCart() {

        RetrofitClient.instance
            .getAllCart()
            .enqueue(object : Callback<List<Cart>> {

                override fun onResponse(
                    call: Call<List<Cart>>,
                    response: Response<List<Cart>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        val pref =
                            getSharedPreferences(
                                "FashionHub",
                                MODE_PRIVATE
                            )

                        val uid =
                            pref.getInt(
                                "uid",
                                0
                            )

                        list.clear()

                        val userCart =
                            response.body()!!
                                .filter {
                                    it.uid == uid
                                }

                        list.addAll(userCart)

                        adapter.notifyDataSetChanged()

                        refreshTotal()

                        txtEmpty.visibility =
                            if (list.isEmpty())
                                View.VISIBLE
                            else
                                View.GONE
                    }
                    else {

                        Toast.makeText(
                            this@CartActivity,
                            "Failed to load cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Cart>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CartActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // REFRESH TOTAL
    // =========================

    override fun refreshTotal() {

        var total = 0.0

        for (item in list) {

            val product =
                productMap[item.pid]

            if (product != null) {

                val price =
                    product.price ?: 0.0

                val qty =
                    item.quantity ?: 1

                total += price * qty
            }
        }

        txtGrandTotal.text =
            "₹ $total"

        txtEmpty.visibility =
            if (list.isEmpty())
                View.VISIBLE
            else
                View.GONE
    }
}