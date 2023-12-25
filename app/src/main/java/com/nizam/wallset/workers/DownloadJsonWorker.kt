package com.nizam.wallset.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nizam.wallset.JSON_FETCHED_KEY
import com.nizam.wallset.URL_FETCHED_FROM_FIREBASE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownloadJsonWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val urlString = inputData.getString(URL_FETCHED_FROM_FIREBASE_KEY)

            val url = URL(urlString)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

            try {
                val inputStream: InputStream = urlConnection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val outputData = workDataOf(JSON_FETCHED_KEY to response.toString())
                Result.success(outputData)
            } catch (e: Exception) {
                Result.retry()
            } finally {
                urlConnection.disconnect()
            }
        }
    }
}