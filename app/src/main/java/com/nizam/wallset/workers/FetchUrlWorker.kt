package com.nizam.wallset.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.database.FirebaseDatabase
import com.nizam.wallset.URL_FETCHED_FROM_FIREBASE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FetchUrlWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val url = firebaseDatabase.getReference("jsonUrl")
                    .get()
                    .await()
                    .value.toString()

                val outputData = workDataOf(URL_FETCHED_FROM_FIREBASE_KEY to url)

                Result.success(outputData)
            } catch (_: Throwable) {
                Result.retry()
            }
        }
    }
}