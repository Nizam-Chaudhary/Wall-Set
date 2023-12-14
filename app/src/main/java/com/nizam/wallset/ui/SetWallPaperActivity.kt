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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        binding.fabApplyWallPaper.setOnClickListener {
            val singleItems =
                arrayOf("Set on Home Screen", "Set on Lock Screen", "Set on Both Screen")
            var checkedItem = 2

            MaterialAlertDialogBuilder(this)
                .setTitle("Apply WallPaper")
                .setNeutralButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Apply") { dialog, _ ->
                    when (checkedItem) {
                        0 -> setWallPaper(getBitmap(), ApplyWallPaperTo.HOME_SCREEN.value)
                        1 -> setWallPaper(getBitmap(), ApplyWallPaperTo.LOCK_SCREEN.value)
                        2 -> setWallPaper(getBitmap(), ApplyWallPaperTo.ON_BOTH_SCREEN.value)
                    }
                }
                .setSingleChoiceItems(singleItems, checkedItem) { dialog, selectedOption ->
                    checkedItem = selectedOption
                }
                .show()
        }
    }

    private fun getBitmap(): Bitmap {
        return binding.imageView.drawable.toBitmap()
    }

    private fun setWallPaper(bitmap: Bitmap, where: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val wallPaperManager = WallpaperManager.getInstance(applicationContext)
            try {
                if (where == ApplyWallPaperTo.ON_BOTH_SCREEN.value) {
                    wallPaperManager.setBitmap(bitmap)
                } else {
                    wallPaperManager.setBitmap(bitmap, null, false, where)
                }
                moveTaskToBack(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private enum class ApplyWallPaperTo(val value: Int) {
        HOME_SCREEN(WallpaperManager.FLAG_SYSTEM),
        LOCK_SCREEN(WallpaperManager.FLAG_LOCK),
        ON_BOTH_SCREEN(0)
    }
}