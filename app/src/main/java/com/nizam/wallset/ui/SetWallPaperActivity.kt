package com.nizam.wallset.ui

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nizam.wallset.R
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

        val materialAlertDialogDrawable = ContextCompat.getDrawable(
            this@SetWallPaperActivity,
            R.drawable.material_alert_diaolog_builder_background
        )

        val attrib = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attrib.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        val url = intent.getStringExtra("url")

        url?.let {
            Glide.with(this)
                .load(url)
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
                            binding.fabApplyWallPaper.setIconTintResource(R.color.white)
                            binding.fabApplyWallPaper.background.setTint(tintedColor)
                            binding.fabApplyWallPaper.visibility = View.VISIBLE

                            val darkTintedColor = ColorUtils.blendARGB(
                                dominantColor,
                                Color.WHITE,
                                0.75f
                            )

                            materialAlertDialogDrawable?.setTint(darkTintedColor)
                        }

                        return false
                    }
                })
                .into(binding.imageView)
        }

        binding.fabApplyWallPaper.setOnClickListener {
            val singleItems =
                arrayOf("On Home Screen", "On Lock Screen", "On Both Screen")
            var checkedItem = 2

            MaterialAlertDialogBuilder(this)
                .setTitle("Apply WallPaper")
                .setBackground(materialAlertDialogDrawable)
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