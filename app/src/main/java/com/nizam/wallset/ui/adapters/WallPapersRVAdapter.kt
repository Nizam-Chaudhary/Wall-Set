package com.nizam.wallset.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.R
import com.nizam.wallset.data.database.ImageItem
import com.nizam.wallset.data.database.entities.Favorite
import com.nizam.wallset.databinding.WallpaperImageItemBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.SetWallPaperActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallPapersRVAdapter(
    var imageItems: List<ImageItem>,
    private val viewModel: MainViewModel,
    private val context: Context) :
    RecyclerView.Adapter<WallPapersRVAdapter.WallPapersViewHolder>() {

        inner class WallPapersViewHolder(view: WallpaperImageItemBinding) : RecyclerView.ViewHolder(view.root) {
            val imageView = view.imageView
            val btnFavorites = view.btnFavorites
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): WallPapersViewHolder {
            return WallPapersViewHolder(WallpaperImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int {
            return imageItems.size
        }

        override fun onBindViewHolder(holder: WallPapersViewHolder, position: Int) {
            val imageItem = imageItems[position]

            CoroutineScope(Dispatchers.IO).launch {
                if (viewModel.isExists(imageItem.url)) {
                    withContext(Dispatchers.Main) {
                        holder.btnFavorites.setImageResource(R.drawable.ic_favorite_filled)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        holder.btnFavorites.setImageResource(R.drawable.ic_favorite)
                    }
                }
            }

            val thumbnailRequest = Glide
                .with(context)
                .load(imageItem.lowResUrl)
                .centerCrop()

            Glide.with(context)
                .load(imageItem.url)
                .placeholder(R.drawable.ic_image_thumbnail)
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

            holder.btnFavorites.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if(viewModel.isExists(imageItem.url)) {
                        viewModel.delete(Favorite(imageItem.url, imageItem.lowResUrl))
                        holder.btnFavorites.setImageResource(R.drawable.ic_favorite)
                    } else {
                        viewModel.upsert(Favorite(imageItem.url, imageItem.lowResUrl))
                        holder.btnFavorites.setImageResource(R.drawable.ic_favorite_filled)
                    }
                }
            }
        }
}