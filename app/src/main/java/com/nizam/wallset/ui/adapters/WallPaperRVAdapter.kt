package com.nizam.wallset.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.data.database.entities.WallPaper
import com.nizam.wallset.databinding.WallpaperItemBinding
import com.nizam.wallset.ui.SetWallPaperActivity

class WallPaperRVAdapter(
    var wallpapers: List<WallPaper>,
    private val context: Context
) : RecyclerView.Adapter<WallPaperRVAdapter.WallPaperViewHolder>() {
    inner class WallPaperViewHolder(view: WallpaperItemBinding) : ViewHolder(view.root) {
        val imageView = view.imageView
        val textView = view.textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallPaperViewHolder {
        return WallPaperViewHolder(WallpaperItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: WallPaperViewHolder, position: Int) {
        val wallpaper = wallpapers[position]

        holder.textView.text = wallpaper.name

        Glide.with(context)
            .load(wallpaper.url)
            .centerCrop() // Apply the centerCrop transformation
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            Intent(context, SetWallPaperActivity::class.java).apply {
                this.putExtra("url", wallpaper.url)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return wallpapers.size
    }
}