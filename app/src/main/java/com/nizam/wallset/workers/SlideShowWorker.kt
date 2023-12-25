package com.nizam.wallset.workers

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nizam.wallset.SLIDE_SHOW_IMAGE_URLS_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class SlideShowWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val imageUrls = inputData.getStringArray(SLIDE_SHOW_IMAGE_URLS_KEY)
                val url = URL(imageUrls?.random())
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                val wallPaperManager = WallpaperManager.getInstance(applicationContext)
                wallPaperManager.setBitmap(bitmap)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }
}