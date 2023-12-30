package com.nizam.wallset.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.nizam.wallset.SLIDE_SHOW_IMAGE_URLS_KEY
import com.nizam.wallset.SLIDE_SHOW_WORKER_NAME
import com.nizam.wallset.data.database.SharedPreferences
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
    application: Application,
    context: Context
) : ViewModel() {

    private val sharedPreferences = SharedPreferences(context)

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

    fun getFavoritesCount() = repository.getFavoritesCount()

    fun startWallPaperSlideShow() {
        CoroutineScope(Dispatchers.IO).launch {
            val inputData =
                workDataOf(SLIDE_SHOW_IMAGE_URLS_KEY to repository.getWallPaperForSlideShow())
            val changeWallPaperRequest =
                PeriodicWorkRequestBuilder<SlideShowWorker>(
                    sharedPreferences.getSlideShowTime(),
                    TimeUnit.MINUTES
                )
                    .setInputData(inputData)
                    .setConstraints(
                        Constraints(
                            requiredNetworkType = NetworkType.CONNECTED,
                        )
                    )
                    .build()

            workManager.enqueueUniquePeriodicWork(
                SLIDE_SHOW_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                changeWallPaperRequest
            )
        }
    }

    fun stopWallPaperSlideShow() {
        workManager.cancelUniqueWork(SLIDE_SHOW_WORKER_NAME)
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