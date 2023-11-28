package com.nizam.wallset.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.nizam.wallset.data.database.entities.WallPaper
import com.nizam.wallset.data.repositories.WallPaperRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException

class MainViewModel (
    private val repository: WallPaperRepository
) : ViewModel(){
    private fun insert(wallPaper: WallPaper) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(wallPaper)
    }

    private fun delete() = CoroutineScope(Dispatchers.Main).launch {
        repository.delete()
    }

    fun getWallPaperByCategories(category: String) = repository.getWallPaperByCategories(category)

    fun getCategoryItems() = repository.getCategoryItems()

    fun getTodayWall() = repository.getTodayWall()

    fun getFourTopPicks() = repository.getFourTopPicks()

    fun download(url: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonArray = repository.downloadAndProcessJsonArray(url)
            delete()

            try {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val owner = jsonObject.getString("owner")
                    val category = jsonObject.getString("category")
                    val imageUrl = jsonObject.getString("url")
                    val topPick = jsonObject.getBoolean("top_pick")
                    val categoryDisplay = jsonObject.getBoolean("category_display")
                    val todayWall = jsonObject.getBoolean("today_wall")

                    val wallPaper = WallPaper(
                        name = name,
                        owner = owner,
                        category = category,
                        url = imageUrl,
                        topPick = topPick,
                        categoryDisplay = categoryDisplay,
                        todayWall = todayWall
                    )

                    insert(wallPaper)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}