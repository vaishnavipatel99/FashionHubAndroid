package com.example.fashionhubapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : BaseActivity() {

    private lateinit var recyclerProducts: RecyclerView

    private lateinit var recyclerCategories: RecyclerView

    private lateinit var productAdapter: ProductAdapter

    private lateinit var etSearch: EditText

    // ORIGINAL LIST

    private var productList = ArrayList<Product>()

    // FILTERED LIST

    private var filteredList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_user_dashboard
        )

        // SETUP DRAWER

        setupDrawer("FashionHub")

        // WINDOW INSETS

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )

            insets
        }

        // USERNAME

        val sharedPreferences =
            getSharedPreferences(
                "FashionHub",
                MODE_PRIVATE
            )

        val username =
            sharedPreferences.getString(
                "name",
                "User"
            )

        // HOME USERNAME

        val txtUsername =
            findViewById<TextView>(
                R.id.txtUsername
            )

        txtUsername.text = username

        // NAV HEADER USERNAME

        val headerView =
            navigationView.getHeaderView(0)

        val txtHeaderUser =
            headerView.findViewById<TextView>(
                R.id.txtHeaderUser
            )

        txtHeaderUser.text =
            "Welcome $username"

        // SEARCH

        etSearch =
            findViewById(R.id.etSearch)

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

        // PRODUCT RECYCLER

        recyclerProducts =
            findViewById(
                R.id.recyclerProducts
            )

        recyclerProducts.layoutManager =
            GridLayoutManager(this, 2)

        recyclerProducts.setHasFixedSize(true)

        // CATEGORY RECYCLER

        recyclerCategories =
            findViewById(
                R.id.recyclerCategories
            )

        recyclerCategories.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        // PRODUCT ADAPTER

        productAdapter =
            ProductAdapter(
                filteredList,
                this
            )

        recyclerProducts.adapter =
            productAdapter

        // LOAD PRODUCTS

        getProducts()
    }

    // =========================
    // GET PRODUCTS
    // =========================

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

                    } else {

                        Toast.makeText(
                            this@UserDashboardActivity,
                            "No Products Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Product>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@UserDashboardActivity,
                        "Error : ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // =========================
    // SEARCH PRODUCTS
    // =========================

    private fun filterProducts(
        query: String
    ) {

        filteredList.clear()

        if (query.isEmpty()) {

            filteredList.addAll(
                productList
            )

        } else {

            for (product in productList) {

                if (
                    product.productName
                        .lowercase()
                        .contains(
                            query.lowercase()
                        )
                ) {

                    filteredList.add(product)
                }
            }
        }

        productAdapter.notifyDataSetChanged()
    }
}