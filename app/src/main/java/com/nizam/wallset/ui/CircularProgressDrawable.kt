package com.nizam.wallset.ui

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.nizam.wallset.R

fun getCircularProgressDrawable(context: Context, centerRadius: Float = 40f): Drawable {
    return CircularProgressDrawable(context).apply {
        this.strokeWidth = 7f
        this.centerRadius = centerRadius
        this.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.circular_progress
            ), PorterDuff.Mode.SRC_IN
        )
        this.start()
    }
}