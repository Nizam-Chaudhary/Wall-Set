package com.nizam.wallset.ui

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nizam.wallset.databinding.ActivitySetWallPaperBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetWallPaperActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetWallPaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetWallPaperBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val attrib = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val url = intent.getStringExtra("url")

        url?.let {
            Glide.with(this)
                .load(url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageView)
        }

        binding.fabSetOnBothScreen.setOnClickListener {
           CoroutineScope(Dispatchers.Default).launch {
               val bitmap = getBitmap()
               val wallPaperManager = WallpaperManager.getInstance(applicationContext)

               try {
                   wallPaperManager.setBitmap(bitmap)
                   moveTaskToBack(true)
               } catch (e: Exception) {
                   e.printStackTrace()
               }
           }
        }

        binding.fabSetOnLockScreen.setOnClickListener {
           CoroutineScope(Dispatchers.Default).launch {
               val bitmap = getBitmap()
               val wallPaperManager = WallpaperManager.getInstance(applicationContext)

               try {
                   wallPaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
                   moveTaskToBack(true)
               } catch (e: Exception) {
                   e.printStackTrace()
               }
           }
        }

        binding.fabSetOnHomeScreen.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                val bitmap = getBitmap()
                val wallPaperManager = WallpaperManager.getInstance(applicationContext)

                try {
                    wallPaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
                    moveTaskToBack(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getBitmap(): Bitmap {
        return binding.imageView.drawable.toBitmap()
    }
}