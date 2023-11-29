package com.nizam.wallset.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.R
import com.nizam.wallset.databinding.RecommendationImageItemBinding

class RecommendationPagerAdapter(
    var imagesUrl: List<String>,
    private val context: Context) :
    RecyclerView.Adapter<RecommendationPagerAdapter.RecommendationPagerViewHolder>() {

    inner class RecommendationPagerViewHolder(view: RecommendationImageItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationPagerViewHolder {
        return RecommendationPagerViewHolder(RecommendationImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imagesUrl.size
    }

    override fun onBindViewHolder(holder: RecommendationPagerViewHolder, position: Int) {
        val imageUrl = imagesUrl[position]

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_image_thumbnail)
            .optionalCenterCrop()
            .thumbnail(0.1f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)
    }
}