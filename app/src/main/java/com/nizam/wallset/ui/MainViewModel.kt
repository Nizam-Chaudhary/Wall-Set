package com.nizam.wallset.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nizam.wallset.SLIDE_SHOW_IMAGE_URLS
import com.nizam.wallset.data.database.SharedPreferences
import com.nizam.wallset.data.database.entities.Favorite
import com.nizam.wallset.data.database.entities.WallPaper
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.workers.SlideShowWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val repository: WallPaperRepository,
    application: Application
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private fun insert(wallPaper: WallPaper) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(wallPaper)
    }

    private fun delete() = CoroutineScope(Dispatchers.Main).launch {
        repository.delete()
    }

    fun getWallPaperByCategories(category: String) = repository.getWallPaperByCategories(category)

    fun getCategoryItems() = repository.getCategoryItems()

    fun getRecommendationWalls() = repository.getRecommendationWalls()


    fun getTopPicks() = repository.getTopPicks()

    fun getAllImages() = repository.getAllImages()

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

                    insert(wallPaper)
                }
                SharedPreferences(context).setLastFetchedDate(
                    arrayOf(
                        Calendar.getInstance().get(Calendar.DATE),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.YEAR)
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun upsert(favorite: Favorite) = CoroutineScope(Dispatchers.IO).launch {
        repository.upsert(favorite)
    }

    fun delete(favorite: Favorite) = CoroutineScope(Dispatchers.IO).launch {
        repository.delete(favorite)
    }

    fun getAllFavorites() = repository.getAllFavorites()

    suspend fun isExists(url: String) = withContext(Dispatchers.IO) {
        repository.isExists(url)
    }

    fun startWallPaperSlideShow() {
        CoroutineScope(Dispatchers.IO).launch {
            val inputData = createInputDataForWallpaper()
            val changeWallPaperRequest =
                PeriodicWorkRequestBuilder<SlideShowWorker>(15L, TimeUnit.MINUTES)
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints(
                            requiredNetworkType = NetworkType.CONNECTED,
                        )
                    )
                    .build()

            workManager.enqueueUniquePeriodicWork(
                "Set SlideShow Image As wallPaper",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                changeWallPaperRequest
            )
        }
    }

    private fun createInputDataForWallpaper(): Data {

        val builder = Data.Builder()
        builder.putStringArray(
            SLIDE_SHOW_IMAGE_URLS,
            repository.getWallPaperForSlideShow()
        )

        return builder.build()

    }
}