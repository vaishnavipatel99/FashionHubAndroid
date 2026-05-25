package com.example.fashionhubapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.model.Category

class UserCategoryAdapter(
    private val list: List<Category>,
    private val listener: OnCategoryClick
) : RecyclerView.Adapter<UserCategoryAdapter.ViewHolder>() {

    interface OnCategoryClick {
        fun onCategoryClick(category: Category)
    }

    class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val txtCategory: TextView =
            view.findViewById(R.id.txtCategory)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_user_category,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.txtCategory.text =
            item.categoryName

        holder.itemView.setOnClickListener {

            listener.onCategoryClick(item)
        }
    }
}