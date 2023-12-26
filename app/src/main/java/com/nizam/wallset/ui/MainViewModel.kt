package com.nizam.wallset.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nizam.wallset.SLIDE_SHOW_IMAGE_URLS_KEY
import com.nizam.wallset.data.database.entities.Favorite
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.workers.LoadImageDataWorker
import com.nizam.wallset.workers.SlideShowWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val repository: WallPaperRepository,
    application: Application
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    fun getWallPaperByCategories(category: String) = repository.getWallPaperByCategories(category)

    fun getCategoryItems() = repository.getCategoryItems()

    fun getRecommendationWalls() = repository.getRecommendationWalls()


    fun getTopPicks() = repository.getTopPicks()

    fun getAllImages() = repository.getAllImages()

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
                PeriodicWorkRequestBuilder<SlideShowWorker>(1, TimeUnit.HOURS)
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints(
                            requiredNetworkType = NetworkType.CONNECTED,
                        )
                    )
                    .build()

            workManager.enqueueUniquePeriodicWork(
                "Set SlideShow Image As wallPaper",
                ExistingPeriodicWorkPolicy.KEEP,
                changeWallPaperRequest
            )
        }
    }

    private fun createInputDataForWallpaper(): Data {

        val builder = Data.Builder()
        builder.putStringArray(
            SLIDE_SHOW_IMAGE_URLS_KEY,
            repository.getWallPaperForSlideShow()
        )

        return builder.build()

    }

    fun startWorkerToLoadData() {

        val loadDataRequest = PeriodicWorkRequestBuilder<LoadImageDataWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "LoadImageDataWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            loadDataRequest
        )
    }
}