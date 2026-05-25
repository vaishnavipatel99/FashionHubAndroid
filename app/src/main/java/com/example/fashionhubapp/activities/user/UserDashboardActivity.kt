package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.user.CartActivity
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.adapters.ProductAdapter
import com.example.fashionhubapp.adapters.UserCategoryAdapter
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Category
import com.example.fashionhubapp.model.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var recyclerCategories: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: UserCategoryAdapter
    private lateinit var bottomNavigation: BottomNavigationView

    private var productList = ArrayList<Product>()
    private var filteredList = ArrayList<Product>()
    private var categoryList = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_dashboard)

        val txtUsername =
            findViewById<TextView>(R.id.txtUsername)

        etSearch =
            findViewById(R.id.etSearch)

        recyclerProducts =
            findViewById(R.id.recyclerProducts)

        recyclerCategories =
            findViewById(R.id.recyclerCategories)

        bottomNavigation =
            findViewById(R.id.bottomNavigation)

        // USERNAME

        val pref =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            )

        txtUsername.text =
            pref.getString(
                "name",
                "User"
            )

        // PRODUCT RECYCLER

        recyclerProducts.layoutManager =
            GridLayoutManager(this, 2)

        productAdapter =
            ProductAdapter(
                filteredList,
                this
            )

        recyclerProducts.adapter =
            productAdapter

        // CATEGORY RECYCLER

        recyclerCategories.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        // SEARCH

        etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filterProducts(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            })

        // BACK PRESS

        onBackPressedDispatcher.addCallback(
            this,

            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    finishAffinity()
                }
            })

        // BOTTOM NAVIGATION

        bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> true

                R.id.nav_cart -> {

                    startActivity(
                        Intent(
                            this,
                            CartActivity::class.java
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

        getProducts()

        getCategories()
    }

    // PRODUCTS

    private fun getProducts() {

        RetrofitClient.instance.getProducts()
            .enqueue(object : Callback<List<Product>> {

                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        productList.clear()

                        filteredList.clear()

                        productList.addAll(
                            response.body()!!
                        )

                        filteredList.addAll(
                            productList
                        )

                        productAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(
                    call: Call<List<Product>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@UserDashboardActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // CATEGORIES

    private fun getCategories() {

        RetrofitClient.instance.getCategories()
            .enqueue(object : Callback<List<Category>> {

                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>
                ) {

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {

                        categoryList.clear()

                        categoryList.add(
                            Category(
                                0,
                                "All",
                                "",
                                "",
                                true
                            )
                        )

                        categoryList.addAll(
                            response.body()!!
                        )

                        categoryAdapter =
                            UserCategoryAdapter(
                                categoryList,

                                object :
                                    UserCategoryAdapter.OnCategoryClick {

                                    override fun onCategoryClick(
                                        category: Category
                                    ) {

                                        filterByCategory(
                                            category.cid
                                        )
                                    }
                                })

                        recyclerCategories.adapter =
                            categoryAdapter
                    }
                }

                override fun onFailure(
                    call: Call<List<Category>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@UserDashboardActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // SEARCH

    private fun filterProducts(
        query: String
    ) {

        filteredList.clear()

        if (query.isEmpty()) {

            filteredList.addAll(productList)

        } else {

            for (product in productList) {

                if (
                    product.productName.lowercase()
                        .contains(query.lowercase())
                ) {

                    filteredList.add(product)
                }
            }
        }

        productAdapter.notifyDataSetChanged()
    }

    // CATEGORY FILTER

    private fun filterByCategory(
        cid: Int
    ) {

        filteredList.clear()

        if (cid == 0) {

            filteredList.addAll(productList)

        } else {

            for (product in productList) {

                if (product.cid == cid) {

                    filteredList.add(product)
                }
            }
        }

        productAdapter.notifyDataSetChanged()
    }
}