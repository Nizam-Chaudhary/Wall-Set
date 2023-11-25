package com.nizam.wallset.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.data.database.CategoryItem
import com.nizam.wallset.databinding.WallpaperItemBinding

class CategoryRVAdapter(
    var categoryItems: List<CategoryItem>,
    private val context: Context
) : RecyclerView.Adapter<CategoryRVAdapter.CategoryRVAdapterHolder>(){
    inner class CategoryRVAdapterHolder(view: WallpaperItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
        val textView = view.textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRVAdapterHolder {
        return CategoryRVAdapterHolder(WallpaperItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CategoryRVAdapterHolder, position: Int) {
        val categoryItem = categoryItems[position]

        holder.textView.text = categoryItem.category

        Glide.with(context)
            .load(categoryItem.url)
            .centerCrop() // Apply the centerCrop transformation
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }
}