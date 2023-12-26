package com.nizam.wallset.data.database

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(
     context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPreferences",Context.MODE_PRIVATE)

}