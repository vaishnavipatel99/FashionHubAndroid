package com.example.fashionhubapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.model.Category

class CategoryAdapter(
    private val list: MutableList<Category>,
    private val listener: CategoryActionListener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    interface CategoryActionListener {
        fun onEdit(category: Category)
        fun onDelete(category: Category)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val details: TextView = view.findViewById(R.id.txtDetails)
        val editBtn: Button = view.findViewById(R.id.btnEdit)
        val deleteBtn: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.name.text = item.categoryName
        holder.details.text = "${item.season} | ${item.occasion}"

        holder.editBtn.setOnClickListener {
            listener.onEdit(item)
        }

        holder.deleteBtn.setOnClickListener {
            listener.onDelete(item)
        }
    }
}