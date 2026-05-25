package com.example.fashionhubapp.activities.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionhubapp.R
import com.example.fashionhubapp.activities.auth.LoginActivity
import com.example.fashionhubapp.adapters.ColorAdapter
import com.example.fashionhubapp.adapters.ImageAdapter
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Cart
import com.example.fashionhubapp.model.CartRequest
import com.example.fashionhubapp.model.ProductImages
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var mainImage: ImageView

    private lateinit var recyclerImages: RecyclerView
    private lateinit var recyclerColors: RecyclerView

    private lateinit var sizeContainer: LinearLayout

    private lateinit var detailColor: TextView

    private lateinit var bottomNavigation: BottomNavigationView

    private var selectedColor = ""
    private var selectedSize = ""
    private var selectedImage = ""

    private lateinit var productImages: ProductImages

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_product_detail)

        // =========================
        // IDS
        // =========================

        mainImage =
            findViewById(R.id.detailImage)

        recyclerImages =
            findViewById(R.id.recyclerImages)

        recyclerColors =
            findViewById(R.id.recyclerColors)

        sizeContainer =
            findViewById(R.id.sizeContainer)

        detailColor =
            findViewById(R.id.detailColor)

        bottomNavigation =
            findViewById(R.id.bottomNavigation)

        val detailName =
            findViewById<TextView>(R.id.detailName)

        val detailPrice =
            findViewById<TextView>(R.id.detailPrice)

        val detailDescription =
            findViewById<TextView>(R.id.detailDescription)

        val detailFabric =
            findViewById<TextView>(R.id.detailFabric)

        val detailStock =
            findViewById<TextView>(R.id.detailStock)

        val addToCartBtn =
            findViewById<Button>(R.id.btnAddToCart)

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        val btnCart =
            findViewById<ImageView>(R.id.btnCart)

        // =========================
        // BACK BUTTON
        // =========================

        btnBack.setOnClickListener {

            finish()
        }

        // =========================
        // CART BUTTON
        // =========================

        btnCart.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CartActivity::class.java
                )
            )
        }

        // =========================
        // GET DATA
        // =========================

        val pid =
            intent.getIntExtra("pid", 0)

        val productName =
            intent.getStringExtra("name") ?: ""

        val productPrice =
            intent.getStringExtra("price") ?: ""

        val description =
            intent.getStringExtra("description") ?: ""

        val fabric =
            intent.getStringExtra("fabric") ?: ""

        val stock =
            intent.getStringExtra("stock") ?: ""

        val sizeString =
            intent.getStringExtra("size") ?: ""

        val imageName =
            intent.getStringExtra("image")
                ?.replace("/images/", "")
                ?: ""

        val imageUrl =
            "http://10.0.2.2:5041/images/$imageName"

        // =========================
        // PRODUCT IMAGES JSON
        // =========================

        val json =
            intent.getStringExtra("productImages") ?: ""

        productImages =
            if (json.isNotEmpty()) {

                Gson().fromJson(
                    json,
                    ProductImages::class.java
                )

            } else {

                ProductImages(
                    emptyList(),
                    emptyMap()
                )
            }

        // =========================
        // DEFAULT IMAGE
        // =========================

        selectedImage = imageName

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(mainImage)

        // =========================
        // PRODUCT INFO
        // =========================

        detailName.text = productName

        detailPrice.text = "₹ $productPrice"

        detailDescription.text = description

        detailFabric.text = "Fabric : $fabric"

        detailStock.text = "In Stock : $stock"

        // =========================
        // SIZE
        // =========================

        val sizes =
            sizeString.split(",")

        createSizeButtons(sizes)

        // =========================
        // COLOR RECYCLER
        // =========================

        recyclerColors.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        val colorList =
            productImages.colors.keys.toList()

        if (colorList.isNotEmpty()) {

            selectedColor = colorList[0]

            detailColor.text =
                "Selected Color : $selectedColor"
        }

        recyclerColors.adapter =
            ColorAdapter(
                colorList,
                this
            ) { colorName ->

                selectedColor =
                    colorName

                detailColor.text =
                    "Selected Color : $selectedColor"

                val images =
                    productImages.colors[colorName]

                if (!images.isNullOrEmpty()) {

                    updateGallery(images)

                    val firstImage =
                        images[0]
                            .replace("/images/", "")

                    selectedImage = firstImage

                    val newUrl =
                        "http://10.0.2.2:5041/images/$firstImage"

                    Glide.with(this)
                        .load(newUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(mainImage)
                }
            }

        // =========================
        // IMAGE GALLERY
        // =========================

        recyclerImages.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        if (productImages.gallery.isNotEmpty()) {

            updateGallery(
                productImages.gallery
            )

        } else {

            updateGallery(
                listOf("/images/$imageName")
            )
        }

        // =========================
        // ADD TO CART
        // =========================

        addToCartBtn.setOnClickListener {

            val pref =
                getSharedPreferences(
                    "FashionHub",
                    MODE_PRIVATE
                )

            val uid =
                pref.getInt("uid", 0)

            if (uid == 0) {

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

                return@setOnClickListener
            }

            if (selectedSize.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please Select Size",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val request =
                CartRequest(
                    uid = uid,
                    pid = pid,
                    sizeSelected = selectedSize,
                    quantity = 1,
                    colorSelected = selectedColor,
                    selectedImage = selectedImage
                )

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

        // =========================
        // BOTTOM NAVIGATION
        // =========================

        bottomNavigation.selectedItemId =
            R.id.nav_home

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
        // BACK PRESS
        // =========================

        onBackPressedDispatcher.addCallback(
            this,

            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    finish()
                }
            })
    }

    // =========================
    // UPDATE IMAGE GALLERY
    // =========================

    private fun updateGallery(
        images: List<String>
    ) {

        recyclerImages.adapter =
            ImageAdapter(
                images,
                this
            ) { clickedImage ->

                val imageName =
                    clickedImage.replace("/images/", "")

                selectedImage = imageName

                val imageUrl =
                    "http://10.0.2.2:5041/images/$imageName"

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(mainImage)
            }
    }

    // =========================
    // SIZE BUTTONS
    // =========================

    private fun createSizeButtons(
        sizes: List<String>
    ) {

        sizeContainer.removeAllViews()

        val sizeViews =
            ArrayList<TextView>()

        for (size in sizes) {

            val textView =
                TextView(this)

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(
                12,
                12,
                12,
                12
            )

            textView.layoutParams =
                params

            textView.text =
                size.trim()

            textView.textSize =
                15f

            textView.setPadding(
                45,
                24,
                45,
                24
            )

            textView.background =
                getDrawable(
                    R.drawable.size_unselected
                )

            textView.setTextColor(
                getColor(R.color.black)
            )

            sizeViews.add(textView)

            textView.setOnClickListener {

                selectedSize =
                    size.trim()

                for (view in sizeViews) {

                    view.background =
                        getDrawable(
                            R.drawable.size_unselected
                        )

                    view.setTextColor(
                        getColor(R.color.black)
                    )
                }

                textView.background =
                    getDrawable(
                        R.drawable.size_selected
                    )

                textView.setTextColor(
                    getColor(R.color.white)
                )
            }

            sizeContainer.addView(textView)
        }

        if (sizeViews.isNotEmpty()) {

            sizeViews[0].performClick()
        }
    }
}