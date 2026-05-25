package com.example.fashionhubapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fashionhubapp.R
import com.example.fashionhubapp.api.RetrofitClient
import com.example.fashionhubapp.model.Cart
import com.example.fashionhubapp.model.CartRequest
import com.example.fashionhubapp.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val list: MutableList<Cart>,
    private val productMap: HashMap<Int, Product>,
    private val context: Context,
    private val listener: CartListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    interface CartListener {
        fun refreshTotal()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.cartImage)

        val name: TextView =
            view.findViewById(R.id.txtName)

        val price: TextView =
            view.findViewById(R.id.txtPrice)

        val total: TextView =
            view.findViewById(R.id.txtTotal)

        val qty: TextView =
            view.findViewById(R.id.txtQty)

        val sizeColor: TextView =
            view.findViewById(R.id.txtSizeColor)

        val plus: TextView =
            view.findViewById(R.id.btnPlus)

        val minus: TextView =
            view.findViewById(R.id.btnMinus)

        val remove: Button =
            view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(context)
                .inflate(
                    R.layout.item_cart,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int =
        list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item =
            list[position]

        val product =
            productMap[item.pid]

        val price =
            product?.price ?: 0.0

        holder.name.text =
            product?.productName ?: "N/A"

        holder.price.text =
            "₹ $price"

        holder.qty.text =
            item.quantity.toString()

        holder.total.text =
            "Total : ₹ ${price * item.quantity}"

        holder.sizeColor.text =
            "Size : ${item.sizeSelected} | Color : ${item.colorSelected}"

        val imageName =
            item.selectedImage
                .replace("/images/", "")

        val imageUrl =
            "http://10.0.2.2:5041/images/$imageName"

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)

        // PLUS

        holder.plus.setOnClickListener {

            item.quantity++

            updateCart(item)

            notifyItemChanged(position)

            listener.refreshTotal()
        }

        // MINUS

        holder.minus.setOnClickListener {

            if (item.quantity > 1) {

                item.quantity--

                updateCart(item)

                notifyItemChanged(position)

                listener.refreshTotal()
            }
        }

        // REMOVE

        holder.remove.setOnClickListener {

            RetrofitClient.instance
                .deleteCart(item.cartId)
                .enqueue(object : Callback<Any> {

                    override fun onResponse(
                        call: Call<Any>,
                        response: Response<Any>
                    ) {

                        list.removeAt(position)

                        notifyItemRemoved(position)

                        listener.refreshTotal()

                        Toast.makeText(
                            context,
                            "Removed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(
                        call: Call<Any>,
                        t: Throwable
                    ) {
                    }
                })
        }
    }

    private fun updateCart(
        item: Cart
    ) {

        val request =
            CartRequest(
                uid = item.uid,
                pid = item.pid,
                sizeSelected = item.sizeSelected,
                quantity = item.quantity,
                colorSelected = item.colorSelected,
                selectedImage = item.selectedImage
            )

        RetrofitClient.instance
            .updateCart(
                item.cartId,
                request
            )
            .enqueue(object : Callback<Any> {

                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                }

                override fun onFailure(
                    call: Call<Any>,
                    t: Throwable
                ) {
                }
            })
    }
}