package com.nizam.wallset.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.nizam.wallset.data.database.SharedPreferences
import com.nizam.wallset.data.database.entities.WallPaper
import com.nizam.wallset.data.repositories.WallPaperRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.util.Calendar

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
    fun download(url: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonArray = repository.downloadAndProcessJsonArray(url)
            delete()

            for(i in 0 until jsonArray.length()) {
                try {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val owner = jsonObject.getString("author")
                    val category = jsonObject.getString("category")
                    val imageUrl = jsonObject.getString("url")

                    val wallPaper = WallPaper(
                        name = name,
                        owner = owner,
                        category = category,
                        url = imageUrl)

                    insert(wallPaper)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            SharedPreferences(context).setLastFetchedDate(
                arrayOf(
                    Calendar.getInstance().get(Calendar.DATE),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.YEAR)
                )
            )
        }
    }
}