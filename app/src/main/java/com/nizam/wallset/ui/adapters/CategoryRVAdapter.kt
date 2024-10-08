package com.nizam.wallset.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nizam.wallset.CATEGORY_NAME
import com.nizam.wallset.data.database.CategoryItem
import com.nizam.wallset.databinding.CategoryItemBinding
import com.nizam.wallset.ui.WallPaperByCategoriesActivity
import com.nizam.wallset.ui.getCircularProgressDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryRVAdapter(
    var categoryItems: List<CategoryItem>,
    private val context: Context
) : RecyclerView.Adapter<CategoryRVAdapter.CategoryRVAdapterHolder>(){
    inner class CategoryRVAdapterHolder(view: CategoryItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
        val textView = view.textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRVAdapterHolder {
        return CategoryRVAdapterHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryRVAdapterHolder, position: Int) {
        val categoryItem = categoryItems[position]

        holder.textView.text = categoryItem.category

        setImage(categoryItem, holder)

        holder.itemView.setOnClickListener {
            Intent(context, WallPaperByCategoriesActivity::class.java).apply {
                this.putExtra(CATEGORY_NAME, categoryItem.category)
                context.startActivity(this)
            }
        }
    }

    private fun setImage(
        categoryItem: CategoryItem,
        holder: CategoryRVAdapterHolder
    ) {
        Glide.with(context)
            .load(categoryItem.lowResUrl)
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
                            Color.GRAY,
                            0.3f
                        )
                        holder.textView.background.setTint(tintedColor)

                        val darkVibrantColor = palette.getDominantColor(Color.TRANSPARENT)
                        val darkTintedColor = ColorUtils.blendARGB(
                            darkVibrantColor,
                            Color.WHITE,
                            0.8f
                        )
                        holder.textView.setTextColor(darkTintedColor)
                    }

                    return false
                }
            })
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }
}