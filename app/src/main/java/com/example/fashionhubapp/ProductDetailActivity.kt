package com.example.fashionhubapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val image = findViewById<ImageView>(R.id.detailImage)
        val name = findViewById<TextView>(R.id.detailName)
        val price = findViewById<TextView>(R.id.detailPrice)
        val description = findViewById<TextView>(R.id.detailDescription)
        val fabric = findViewById<TextView>(R.id.detailFabric)
        val color = findViewById<TextView>(R.id.detailColor)
        val size = findViewById<TextView>(R.id.detailSize)
        val stock = findViewById<TextView>(R.id.detailStock)

        val productImage = intent.getStringExtra("image")

        val imageName =
            intent.getStringExtra("image")!!.replace("/images/", "")

        val imageUrl =
            "http://10.0.2.2:5041/images/$imageName"

        Glide.with(this)
            .load(imageUrl)
            .into(image)

        name.text = intent.getStringExtra("name")
        price.text = "₹ ${intent.getStringExtra("price")}"
        description.text = intent.getStringExtra("description")
        fabric.text = "Fabric : ${intent.getStringExtra("fabric")}"
        color.text = "Color : ${intent.getStringExtra("color")}"
        size.text = "Size : ${intent.getStringExtra("size")}"
        stock.text = "Stock : ${intent.getStringExtra("stock")}"
    }
}