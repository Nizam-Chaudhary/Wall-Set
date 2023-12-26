package com.nizam.wallset.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nizam.wallset.JSON_FETCHED_KEY
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.database.entities.WallPaper
import com.nizam.wallset.data.repositories.WallPaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class ParseAndStoreJsonWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val jsonArray = JSONArray(inputData.getString(JSON_FETCHED_KEY))

                val database = WallPaperDatabase(applicationContext)
                val repository = WallPaperRepository(database)
                repository.delete()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val owner = jsonObject.getString("owner")
                    val category = jsonObject.getString("category")
                    val imageUrl = jsonObject.getString("url")
                    val lowResUrl = jsonObject.getString("low_res_url")
                    val topPick = jsonObject.getBoolean("top_pick")
                    val categoryDisplay = jsonObject.getBoolean("category_display")
                    val todayWall = jsonObject.getBoolean("today_wall")

                    val wallPaper = WallPaper(
                        name = name,
                        owner = owner,
                        category = category,
                        url = imageUrl,
                        lowResUrl = lowResUrl,
                        topPick = topPick,
                        categoryDisplay = categoryDisplay,
                        todayWall = todayWall
                    )

                    repository.insert(wallPaper)
                }
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
}