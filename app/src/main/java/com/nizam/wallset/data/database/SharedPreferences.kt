package com.nizam.wallset.data.database

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(
     context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPreferences",Context.MODE_PRIVATE)

    private val fetchedDayOfMonth = "FetchedDayOfMonth"
    private val fetchMonth = "FetchedMonth"
    private val fetchYear = "FetchedYear"

    fun setLastFetchedDate(date: Array<Int>) {
        sharedPreferences.edit().apply {
            this.putInt(fetchedDayOfMonth,date[0])
            this.putInt(fetchMonth,date[1])
            this.putInt(fetchYear,date[2])
            this.apply()
        }
    }

    fun toLoad(date: Array<Int>) = sharedPreferences.getInt(fetchedDayOfMonth, 0) < date[0] ||
        sharedPreferences.getInt(fetchMonth, 0) < date[1] || sharedPreferences.getInt(fetchYear, 0) < date [2]

    fun getVal() = arrayOf(
        sharedPreferences.getInt(fetchedDayOfMonth,0),
        sharedPreferences.getInt(fetchMonth,0),
        sharedPreferences.getInt(fetchYear,0),
        )
}