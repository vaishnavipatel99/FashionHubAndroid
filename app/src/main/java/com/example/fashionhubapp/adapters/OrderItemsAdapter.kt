package com.example.fashionhubapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.model.OrderItemResponse
import com.google.android.material.button.MaterialButton

class OrderItemsAdapter(
    private val list: List<OrderItemResponse>,
    private val onReviewClick: (OrderItemResponse) -> Unit
) : RecyclerView.Adapter<OrderItemsAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.txtName)
        val qty: TextView = view.findViewById(R.id.txtQty)
        val size: TextView = view.findViewById(R.id.txtSize)
        val color: TextView = view.findViewById(R.id.txtColor)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val btnReview: MaterialButton =
            view.findViewById(R.id.btnReview)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)

        return VH(view)
    }

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
        holder.btnReview.setOnClickListener {
            onReviewClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}