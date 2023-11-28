package com.nizam.wallset.ui

import android.app.WallpaperManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.databinding.ActivitySetWallPaperBinding

class SetWallPaperActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetWallPaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetWallPaperBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val url = intent.getStringExtra("url")

        url?.let {
            Glide.with(this)
                .load(url)
                .thumbnail(0.1f)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageView)
        }

        binding.fabSetOnBothScreen.setOnClickListener {
            val bitmap = binding.imageView.drawable.toBitmap()
            val wallPaperManager = WallpaperManager.getInstance(this.applicationContext)

            try {
                wallPaperManager.setBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.fabSetOnLockScreen.setOnClickListener {
            val bitmap = binding.imageView.drawable.toBitmap()
            val wallPaperManager = WallpaperManager.getInstance(this.applicationContext)

            try {
                wallPaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.fabSetOnHomeScreen.setOnClickListener {
            val bitmap = binding.imageView.drawable.toBitmap()
            val wallPaperManager = WallpaperManager.getInstance(this.applicationContext)

            try {
                wallPaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}