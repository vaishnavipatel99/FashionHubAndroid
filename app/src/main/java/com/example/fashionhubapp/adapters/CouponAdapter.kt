package com.example.fashionhubapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.model.CouponResponse
import com.google.android.material.button.MaterialButton

class CouponAdapter(

    private val context: Context,

    private val list: List<CouponResponse>,

    private val onApplyClick: (CouponResponse) -> Unit

) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {

    class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        val txtCode: TextView =
            view.findViewById(R.id.txtCouponCode)

        val txtDesc: TextView =
            view.findViewById(R.id.txtCouponDesc)

        val btnApply: MaterialButton =
            view.findViewById(R.id.btnApply)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(context)
                .inflate(
                    R.layout.item_coupon,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.txtCode.text =
            item.couponCode

        holder.txtDesc.text =
            "Get ₹${item.discountType} OFF on minimum ₹${item.minimumOrderAmount}"

        holder.btnApply.setOnClickListener {

            onApplyClick(item)

            holder.btnApply.text =
                "APPLIED"

            holder.btnApply.isEnabled =
                false
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }
}