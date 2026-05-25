package com.example.fashionhubapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R

class ColorAdapter(
    private val list: List<String>,
    private val context: Context,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val colorName: TextView =
            view.findViewById(R.id.txtColor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(context)
                .inflate(
                    R.layout.item_color,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val color =
            list[position]

        holder.colorName.text =
            color

        holder.itemView.setOnClickListener {

            onClick(color)
        }
    }

    override fun getItemCount(): Int =
        list.size
}