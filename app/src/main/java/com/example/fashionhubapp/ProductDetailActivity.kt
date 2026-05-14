package com.example.fashionhubapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Cart
import com.example.fashionhubapp.model.CartRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // =========================
        // NAV DRAWER
        // =========================

        setupDrawer("Product Detail")

        // =========================
        // FIND VIEWS
        // =========================

        val image = findViewById<ImageView>(R.id.detailImage)

        val name = findViewById<TextView>(R.id.detailName)

        val price = findViewById<TextView>(R.id.detailPrice)

        val description =
            findViewById<TextView>(R.id.detailDescription)

        val fabric =
            findViewById<TextView>(R.id.detailFabric)

        val color =
            findViewById<TextView>(R.id.detailColor)

        val size =
            findViewById<TextView>(R.id.detailSize)

        val stock =
            findViewById<TextView>(R.id.detailStock)

        val addToCartBtn =
            findViewById<Button>(R.id.btnAddToCart)

        // =========================
        // GET DATA
        // =========================

        val pid =
            intent.getIntExtra("pid", 0)

        val imageName =
            intent.getStringExtra("image")
                ?.replace("/images/", "") ?: ""

        val imageUrl =
            "http://10.0.2.2:5041/images/$imageName"

        // =========================
        // LOAD IMAGE
        // =========================

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(image)

        // =========================
        // SET DATA
        // =========================

        name.text =
            intent.getStringExtra("name")

        price.text =
            "₹ ${intent.getStringExtra("price")}"

        description.text =
            intent.getStringExtra("description")

        fabric.text =
            "Fabric : ${intent.getStringExtra("fabric")}"

        color.text =
            "Color : ${intent.getStringExtra("color")}"

        size.text =
            "Size : ${intent.getStringExtra("size")}"

        stock.text =
            "Stock : ${intent.getStringExtra("stock")}"

        // =========================
        // ADD TO CART
        // =========================

        addToCartBtn.setOnClickListener {

            // IMPORTANT
            val pref =
                getSharedPreferences(
                    "FashionHub",
                    MODE_PRIVATE
                )

            val uid =
                pref.getInt("uid", 0)

            // LOGIN CHECK
            if (uid == 0) {

                Toast.makeText(
                    this,
                    "Please Login First",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

                return@setOnClickListener
            }

            // CREATE REQUEST
            val request =
                CartRequest(
                    uid = uid,
                    pid = pid,
                    sizeSelected = "M",
                    quantity = 1,
                    colorSelected = "Black"
                )

            // API CALL
            RetrofitClient.instance
                .addToCart(request)
                .enqueue(object : Callback<Cart> {

                    override fun onResponse(
                        call: Call<Cart>,
                        response: Response<Cart>
                    ) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@ProductDetailActivity,
                                "Added To Cart",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    this@ProductDetailActivity,
                                    CartActivity::class.java
                                )
                            )

                        } else {

                            Toast.makeText(
                                this@ProductDetailActivity,
                                "Failed To Add Cart",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<Cart>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@ProductDetailActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}