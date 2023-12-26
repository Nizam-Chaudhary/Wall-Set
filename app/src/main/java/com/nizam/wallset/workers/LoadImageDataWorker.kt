package com.nizam.wallset.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.database.entities.WallPaper
import com.nizam.wallset.data.repositories.WallPaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LoadImageDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = getUrlFromFirebase()

                val response = getJsonString(urlString)

                parseAndStoreJson(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Result.success()
        }
    }

    private suspend fun getUrlFromFirebase(): String {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        return firebaseDatabase.getReference("jsonUrl")
            .get()
            .await()
            .value.toString()
    }

    private fun getJsonString(urlString: String): String {
        val url = URL(urlString)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        val inputStream: InputStream = urlConnection.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val response = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        return response.toString()
    }

    private suspend fun parseAndStoreJson(response: String) {
        val jsonArray = JSONArray(response)

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
    }
}