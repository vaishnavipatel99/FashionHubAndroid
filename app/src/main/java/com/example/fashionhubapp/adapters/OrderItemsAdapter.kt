package com.example.fashionhubapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.model.OrderItemResponse

class OrderItemsAdapter(
    private val list: List<OrderItemResponse>
) : RecyclerView.Adapter<OrderItemsAdapter.VH>() {

    // ================= VIEW HOLDER =================
    class VH(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.txtName)
        val qty: TextView = view.findViewById(R.id.txtQty)
        val size: TextView = view.findViewById(R.id.txtSize)
        val color: TextView = view.findViewById(R.id.txtColor)
        val price: TextView = view.findViewById(R.id.txtPrice)
    }

    // ================= CREATE VIEW =================
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)

        return VH(view)
    }

    // ================= BIND DATA =================
    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {

        val item = list[position]

        holder.name.text = item.productName
        holder.qty.text = "Qty: ${item.quantity}"
        holder.size.text = "Size: ${item.sizeSelected}"
        holder.color.text = "Color: ${item.colorSelected}"
        holder.price.text = "₹${item.price}"
    }

    // ================= SIZE =================
    override fun getItemCount(): Int {
        return list.size
    }
}