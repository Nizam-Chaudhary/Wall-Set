package com.nizam.wallset.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.R
import com.nizam.wallset.databinding.ImageItemBinding
import com.nizam.wallset.ui.SetWallPaperActivity

class RecyclerPagerAdapter(
    var imagesUrl: List<String>,
    private val context: Context) :
    RecyclerView.Adapter<RecyclerPagerAdapter.RecommendationPagerViewHolder>() {

    inner class RecommendationPagerViewHolder(view: ImageItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationPagerViewHolder {
        return RecommendationPagerViewHolder(ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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

        holder.itemView.setOnClickListener {
            Intent(context, SetWallPaperActivity::class.java).apply {
                this.putExtra("url", imageUrl)
                context.startActivity(this)
            }
        }
    }
}