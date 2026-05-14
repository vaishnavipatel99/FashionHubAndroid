package com.example.fashionhubapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionhubapp.model.Product

class ProductAdapter(
    private val list: List<Product>,
    private val context: Context
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.productName)
        val price: TextView = view.findViewById(R.id.productPrice)
        val image: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val product = list[position]

        holder.name.text = product.productName
        holder.price.text = "₹ ${product.price}"

        // REMOVE /images/ FROM DATABASE VALUE
        val imageName =
            product.productImage.replace("/images/", "")

        // FINAL IMAGE URL
        val imageUrl =
            "http://10.0.2.2:5041/images/$imageName"

        println("IMAGE URL = $imageUrl")

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)

        // CLICK EVENT
        holder.itemView.setOnClickListener {

            val intent =
                Intent(context, ProductDetailActivity::class.java)

            intent.putExtra("pid", product.pid)
            intent.putExtra("name", product.productName)
            intent.putExtra("price", product.price.toString())
            intent.putExtra("description", product.description)
            intent.putExtra("fabric", product.fabric)
            intent.putExtra("color", product.color)
            intent.putExtra("size", product.size)
            intent.putExtra("stock", product.stock.toString())
            intent.putExtra("image", imageName)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}