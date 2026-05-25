package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.adapters.OrderAdapter
import com.example.fashionhubapp.adapters.OrderItemsAdapter
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Order
import com.example.fashionhubapp.model.OrderItemResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersActivity : AppCompatActivity() {

    private lateinit var recyclerOrders: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var emptyLayout: LinearLayout

    private var orderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // ================= IDS =================
        recyclerOrders = findViewById(R.id.recyclerOrders)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        emptyLayout = findViewById(R.id.emptyLayout)

        val btnMenu = findViewById<ImageView>(R.id.btnMenu)

        // ================= MENU =================
        btnMenu.setOnClickListener {
            Toast.makeText(this, "FashionHub Orders", Toast.LENGTH_SHORT).show()
        }

        // ================= RECYCLER =================
        recyclerOrders.layoutManager = LinearLayoutManager(this)

        adapter = OrderAdapter(orderList) { order ->
            showOrderItems(order.oid)
        }

        recyclerOrders.adapter = adapter

        // ================= BOTTOM NAV =================
        bottomNavigation.selectedItemId = R.id.nav_orders

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, UserDashboardActivity::class.java))
                    true
                }

                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }

                R.id.nav_orders -> true

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.nav_logout -> {
                    getSharedPreferences("FashionHub", MODE_PRIVATE)
                        .edit().clear().apply()

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }

        // ================= LOAD ORDERS =================
        getOrders()

        // ================= BACK PRESS =================
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }
        )
    }

    // ================= GET ORDERS =================
    private fun getOrders() {

        val username = getSharedPreferences("FashionHub", MODE_PRIVATE)
            .getString("name", "") ?: ""

        RetrofitClient.instance.getOrders()
            .enqueue(object : Callback<List<Order>> {

                override fun onResponse(
                    call: Call<List<Order>>,
                    response: Response<List<Order>>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        orderList.clear()

                        val userOrders = response.body()!!.filter {
                            it.username.equals(username, ignoreCase = true)
                        }

                        orderList.addAll(userOrders)
                        adapter.notifyDataSetChanged()

                        emptyLayout.visibility =
                            if (orderList.isEmpty()) View.VISIBLE else View.GONE

                    } else {
                        emptyLayout.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                    Toast.makeText(this@OrdersActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    // ================= ORDER ITEMS POPUP =================
    private fun showOrderItems(oid: Int) {

        RetrofitClient.instance.getOrderItems(oid)
            .enqueue(object : Callback<List<OrderItemResponse>> {

                override fun onResponse(
                    call: Call<List<OrderItemResponse>>,
                    response: Response<List<OrderItemResponse>>
                ) {

                    if (!response.isSuccessful || response.body() == null) {
                        Toast.makeText(this@OrdersActivity, "No items found", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val items = response.body()!!

                    val dialog = BottomSheetDialog(this@OrdersActivity)
                    val view = layoutInflater.inflate(R.layout.bottom_order_items, null)

                    val recycler = view.findViewById<RecyclerView>(R.id.recyclerItems)

                    recycler.layoutManager = LinearLayoutManager(this@OrdersActivity)
                    recycler.adapter = OrderItemsAdapter(items)

                    dialog.setContentView(view)
                    dialog.show()
                }

                override fun onFailure(call: Call<List<OrderItemResponse>>, t: Throwable) {
                    Toast.makeText(this@OrdersActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}