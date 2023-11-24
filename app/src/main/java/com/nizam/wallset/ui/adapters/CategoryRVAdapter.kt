package com.nizam.wallset.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.nizam.wallset.data.database.CategoryItem
import com.nizam.wallset.databinding.CategoryItemBinding

class CategoryRVAdapter(
    var categoryItems: List<CategoryItem>,
    private val context: Context
) : RecyclerView.Adapter<CategoryRVAdapter.CategoryRVAdapterHolder>(){
    inner class CategoryRVAdapterHolder(view: CategoryItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
        val tvCategory = view.tvCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRVAdapterHolder {
        return CategoryRVAdapterHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CategoryRVAdapterHolder, position: Int) {
        val categoryItem = categoryItems[position]

        println(categoryItem.category)
        holder.tvCategory.text = categoryItem.category

        Glide.with(context).load(categoryItem.url).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }
}