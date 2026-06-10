package com.example.fashionhubapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionhubapp.R
import com.example.fashionhubapp.model.ReviewResponse

class ReviewAdapter(
    private val reviews: List<ReviewResponse>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtUser: TextView =
            itemView.findViewById(R.id.txtUser)

        val txtReview: TextView =
            itemView.findViewById(R.id.txtReview)

        val txtDate: TextView =
            itemView.findViewById(R.id.txtDate)

        val ratingBar: RatingBar =
            itemView.findViewById(R.id.ratingBarReview)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_review,
                parent,
                false
            )

        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReviewViewHolder,
        position: Int
    ) {

        val review = reviews[position]

        holder.txtUser.text =
            review.userName

        holder.txtReview.text =
            review.feedback

        holder.txtDate.text =
            review.createdAt

        holder.ratingBar.rating =
            review.rating.toFloat()

        holder.ratingBar.progressTintList =
            android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#FFC107")
            )
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}