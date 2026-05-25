package com.example.fashionhubapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.model.Order

class OrderAdapter(
    private val list: List<Order>,
    private val onClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtOrderId: TextView = view.findViewById(R.id.txtOrderId)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val txtPayment: TextView = view.findViewById(R.id.txtPayment)
        val txtAddress: TextView = view.findViewById(R.id.txtAddress)
        val txtDelivery: TextView = view.findViewById(R.id.txtDelivery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        val order = list[position]

        holder.txtOrderId.text = "Order #${order.oid}"
        holder.txtAmount.text = "₹${order.amount}"
        holder.txtStatus.text = "Status : ${order.orderStatus}"
        holder.txtPayment.text = "Payment : ${order.paymentStatus}"
        holder.txtAddress.text = "Address : ${order.deliveryAddress}"
        holder.txtDelivery.text = "Delivery : ${order.estimatedDelivery}"

        holder.itemView.setOnClickListener {
            onClick(order)
        }
    }
}