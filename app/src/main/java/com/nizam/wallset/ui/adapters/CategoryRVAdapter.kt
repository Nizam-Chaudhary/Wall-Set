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
import com.nizam.wallset.data.database.CategoryItem
import com.nizam.wallset.databinding.CategoryItemBinding
import com.nizam.wallset.ui.WallPaperByCategoriesActivity

class CategoryRVAdapter(
    var categoryItems: List<CategoryItem>,
    private val context: Context
) : RecyclerView.Adapter<CategoryRVAdapter.CategoryRVAdapterHolder>(){
    inner class CategoryRVAdapterHolder(view: CategoryItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
        val textView = view.textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRVAdapterHolder {
        return CategoryRVAdapterHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CategoryRVAdapterHolder, position: Int) {
        val categoryItem = categoryItems[position]

        holder.textView.text = categoryItem.category

        val thumbnailRequest = Glide
            .with(context)
            .load(categoryItem.lowResUrl)
            .centerCrop()

        Glide.with(context)
            .load(categoryItem.url)
            .thumbnail(thumbnailRequest)
            .placeholder(R.drawable.ic_image_thumbnail)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            Intent(context, WallPaperByCategoriesActivity::class.java).apply {
                this.putExtra("category", categoryItem.category)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }
}