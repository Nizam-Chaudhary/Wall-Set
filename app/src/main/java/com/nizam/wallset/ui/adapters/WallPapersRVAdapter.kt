package com.nizam.wallset.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nizam.wallset.R
import com.nizam.wallset.data.database.ImageItem
import com.nizam.wallset.data.database.entities.Favorite
import com.nizam.wallset.databinding.WallpaperImageItemBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.SetWallPaperActivity
import com.nizam.wallset.ui.getCircularProgressDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WallPapersRVAdapter(
    var imageItems: List<ImageItem>,
    private val viewModel: MainViewModel,
    private val context: Context) :
    RecyclerView.Adapter<WallPapersRVAdapter.WallPapersViewHolder>() {

    companion object {
        val urlChangedListFromWallPaper = mutableListOf<ImageItem>()
        val urlChangedListFromCategories = mutableListOf<ImageItem>()
        val urlChangedListFromTopPicks = mutableListOf<ImageItem>()
        var isChangedFromCategory = false
        var isChangedFromTopPicks = false
        var isChangedFromWallPaper = false
    }

    inner class WallPapersViewHolder(view: WallpaperImageItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        val imageView = view.imageView
        val btnFavorites = view.btnFavorites
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallPapersViewHolder {
        return WallPapersViewHolder(
            WallpaperImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: WallPapersViewHolder, position: Int) {
        val imageItem = imageItems[position]

        val favoriteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_favorite)
        val filledFavoriteDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_favorite_filled)

        setImage(imageItem, favoriteDrawable, filledFavoriteDrawable, holder)

        CoroutineScope(Dispatchers.IO).launch {
            if (viewModel.isExists(imageItem.url)) {
                withContext(Dispatchers.Main) {
                    holder.btnFavorites.setImageDrawable(filledFavoriteDrawable)
                }
            } else {
                withContext(Dispatchers.Main) {
                    holder.btnFavorites.setImageDrawable(favoriteDrawable)
                }
            }
        }

        holder.itemView.setOnClickListener {
            Intent(context, SetWallPaperActivity::class.java).apply {
                this.putExtra("url", imageItem.url)
                context.startActivity(this)
            }
        }

        holder.btnFavorites.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (!isChangedFromCategory) urlChangedListFromCategories.clear()
                if (!isChangedFromWallPaper) urlChangedListFromWallPaper.clear()
                if (!isChangedFromTopPicks) urlChangedListFromTopPicks.clear()

                if (viewModel.isExists(imageItem.url)) {
                    viewModel.delete(Favorite(imageItem.url, imageItem.lowResUrl))
                    holder.btnFavorites.setImageDrawable(favoriteDrawable)

                    urlChangedListFromWallPaper.add(imageItem)
                    urlChangedListFromTopPicks.add(imageItem)
                    urlChangedListFromCategories.add(imageItem)
                    isChangedFromCategory = true
                    isChangedFromTopPicks = true
                    isChangedFromWallPaper = true
                } else {
                    viewModel.upsert(Favorite(imageItem.url, imageItem.lowResUrl))
                    holder.btnFavorites.setImageDrawable(filledFavoriteDrawable)
                }
                urlChangedListFromWallPaper.add(imageItem)
                urlChangedListFromTopPicks.add(imageItem)
                urlChangedListFromCategories.add(imageItem)
                isChangedFromCategory = true
                isChangedFromTopPicks = true
                isChangedFromWallPaper = true
            }
        }
    }

    private fun setImage(
        imageItem: ImageItem,
        favoriteDrawable: Drawable?,
        filledFavoriteDrawable: Drawable?,
        holder: WallPapersViewHolder
    ) {
        Glide
            .with(context)
            .load(imageItem.lowResUrl)
            .placeholder(getCircularProgressDrawable(context))
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Use coroutines for Palette generation
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        val palette = Palette.from(bitmap).generate()
                        val dominantColor = palette.getDominantColor(Color.TRANSPARENT)

                        val tintedColor = ColorUtils.blendARGB(
                            dominantColor,
                            Color.WHITE,
                            0.3f
                        )

                        // Assuming favoriteDrawable and filledFavoriteDrawable are Drawables
                        favoriteDrawable?.setTint(tintedColor)
                        filledFavoriteDrawable?.setTint(tintedColor)
                    }

                    return false
                }
            })
            .into(holder.imageView)
    }
}