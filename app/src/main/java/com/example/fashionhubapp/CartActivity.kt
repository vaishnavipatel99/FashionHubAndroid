package com.example.fashionhubapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Cart
import com.example.fashionhubapp.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : BaseActivity(),
    CartAdapter.CartListener {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CartAdapter

    private lateinit var txtGrandTotal: TextView
    private lateinit var txtEmpty: TextView
    private lateinit var btnCheckout: Button

    private var list = mutableListOf<Cart>()
    private var productMap = HashMap<Int, Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        //  NAV BAR ENABLE
        setupDrawer("My Cart")

        recycler = findViewById(R.id.cartRecycler)
        txtGrandTotal = findViewById(R.id.txtGrandTotal)
        txtEmpty = findViewById(R.id.txtEmpty)
        btnCheckout = findViewById(R.id.btnCheckout)

        recycler.layoutManager = LinearLayoutManager(this)

        adapter = CartAdapter(list, productMap, this, this)
        recycler.adapter = adapter

        loadProducts()

        btnCheckout.setOnClickListener {

            if (list.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("TOTAL", txtGrandTotal.text.toString().replace("₹", "").trim().toDouble())
            startActivity(intent)
        }
    }

    private fun loadProducts() {
        RetrofitClient.instance.getProducts()
            .enqueue(object : Callback<List<Product>> {

                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {

                    if (response.isSuccessful && response.body() != null) {

                        productMap.clear()

                        for (p in response.body()!!) {
                            productMap[p.pid] = p
                        }

                        loadCart()
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Toast.makeText(this@CartActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadCart() {

        RetrofitClient.instance.getAllCart()
            .enqueue(object : Callback<List<Cart>> {

                override fun onResponse(call: Call<List<Cart>>, response: Response<List<Cart>>) {

                    if (response.isSuccessful && response.body() != null) {

                        val pref = getSharedPreferences("FashionHub", MODE_PRIVATE)
                        val uid = pref.getInt("uid", 0)

                        list.clear()

                        val userCart = response.body()!!.filter { it.uid == uid }

                        list.addAll(userCart)

                        adapter.notifyDataSetChanged()

                        refreshTotal()

                        txtEmpty.visibility =
                            if (list.isEmpty()) TextView.VISIBLE else TextView.GONE
                    }
                }

                override fun onFailure(call: Call<List<Cart>>, t: Throwable) {
                    Toast.makeText(this@CartActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun refreshTotal() {

        var total = 0.0

        for (item in list) {
            val product = productMap[item.pid]
            val price = product?.price ?: 0.0
            total += price * item.quantity
        }

        txtGrandTotal.text = "₹ $total"

        txtEmpty.visibility =
            if (list.isEmpty()) TextView.VISIBLE else TextView.GONE
    }
}