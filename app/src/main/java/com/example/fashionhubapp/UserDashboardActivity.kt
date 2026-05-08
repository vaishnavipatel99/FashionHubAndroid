package com.example.fashionhubapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    // PRODUCT LIST
    private var productList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_user_dashboard)

        // WINDOW INSETS
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // RECYCLER VIEW
        recyclerProducts =
            findViewById(R.id.recyclerProducts)

        recyclerProducts.layoutManager =
            LinearLayoutManager(this)

        recyclerProducts.setHasFixedSize(true)

        // ADAPTER
        productAdapter =
            ProductAdapter(productList, this)

        recyclerProducts.adapter =
            productAdapter

        // LOAD PRODUCTS
        getProducts()
    }

    private fun getProducts() {

        RetrofitClient.instance.getProducts()
            .enqueue(object : Callback<List<Product>> {

                override fun onResponse(
                    call: Call<List<Product>>,
                    response: Response<List<Product>>
                ) {

                    if (response.isSuccessful &&
                        response.body() != null
                    ) {

                        // CLEAR OLD DATA
                        productList.clear()

                        // ADD NEW DATA
                        productList.addAll(response.body()!!)

                        // REFRESH ADAPTER
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
}