package com.nizam.wallset.data.database

import android.content.Context
import android.content.SharedPreferences
import com.nizam.wallset.SLIDE_SHOW_CHANGE_TIME
import com.nizam.wallset.SLIDE_SHOW_KEY

class SharedPreferences(
     context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)

    fun setSlideShowStatus(isEnabled: Boolean) =
        sharedPreferences.edit().putBoolean(SLIDE_SHOW_KEY, isEnabled).apply()

    fun setSlideShowChangeTime(timeInMinutes: Long) =
        sharedPreferences.edit().putLong(SLIDE_SHOW_CHANGE_TIME, timeInMinutes).apply()

    fun getSlideShowStatus() = sharedPreferences.getBoolean(SLIDE_SHOW_CHANGE_TIME, false)

    fun getSlideShowTime() = sharedPreferences.getLong(SLIDE_SHOW_CHANGE_TIME, 15L)
}