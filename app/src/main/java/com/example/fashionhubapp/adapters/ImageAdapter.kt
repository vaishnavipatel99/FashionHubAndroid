// ImageAdapter.kt

package com.example.fashionhubapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionhubapp.R

class ImageAdapter(
    private val list: List<String>,
    private val context: Context,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.imgItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(context)
                .inflate(
                    R.layout.item_image,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val imageName =
            list[position]
                .replace("/images/", "")

        val imageUrl =
            "http://10.0.2.2:5041/images/$imageName"

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.itemView.setOnClickListener {

            onClick(list[position])
        }
    }

    override fun getItemCount(): Int =
        list.size
}