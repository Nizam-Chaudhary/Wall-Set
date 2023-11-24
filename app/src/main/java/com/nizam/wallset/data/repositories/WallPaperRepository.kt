package com.nizam.wallset.data.repositories

import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.database.entities.WallPaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WallPaperRepository(
    private val database: WallPaperDatabase
) {
    suspend fun insert(wallPaper: WallPaper) = database.getWallPaperDao().insert(wallPaper)

    suspend fun delete() = database.getWallPaperDao().delete()

    fun getWallPaperByCategories(category: String) = database.getWallPaperDao().getWallPaperByCategories(category)

    fun getCategories() = database.getWallPaperDao().getCategories()

    fun getDisplayWallForCategories(category: String) = database.getWallPaperDao().getDisplayWallForCategories(category)

    suspend fun downloadAndProcessJsonArray(url: String): JSONArray {
        return withContext(Dispatchers.IO) {
            val jsonResponse = downloadJson(url)
            JSONArray(jsonResponse)
        }
    }

    private fun downloadJson(urlString: String): String {
        val url = URL(urlString)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

        return try {
            val inputStream: InputStream = urlConnection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
                println("JSON LINE : $line")
            }


            response.toString()
        } finally {
            urlConnection.disconnect()
        }
    }
}