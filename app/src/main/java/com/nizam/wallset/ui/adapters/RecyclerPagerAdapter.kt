package com.nizam.wallset.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.R
import com.nizam.wallset.data.database.ImageItem
import com.nizam.wallset.databinding.ImageItemBinding
import com.nizam.wallset.ui.SetWallPaperActivity
import com.nizam.wallset.ui.getCircularProgressDrawable

class RecyclerPagerAdapter(
    var imageItems: List<ImageItem>,
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
        return imageItems.size
    }

    override fun onBindViewHolder(holder: RecommendationPagerViewHolder, position: Int) {
        val imageItem = imageItems[position]

        val thumbnailRequest = Glide
            .with(context)
            .load(imageItem.lowResUrl)
            .priority(Priority.HIGH)
            .centerCrop()

        val circularProgress = CircularProgressDrawable(context)
        circularProgress.strokeWidth = 7f
        circularProgress.centerRadius = 40f
        circularProgress.setColorFilter(
            ContextCompat.getColor(context, R.color.circular_progress), PorterDuff.Mode.SRC_IN
        )
        circularProgress.start()

        Glide.with(context)
            .load(imageItem.url)
            .priority(Priority.LOW)
            .placeholder(getCircularProgressDrawable(context))
            .centerCrop()
            .thumbnail(thumbnailRequest)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            Intent(context, SetWallPaperActivity::class.java).apply {
                this.putExtra("url", imageItem.url)
                context.startActivity(this)
            }
        }
    }
}