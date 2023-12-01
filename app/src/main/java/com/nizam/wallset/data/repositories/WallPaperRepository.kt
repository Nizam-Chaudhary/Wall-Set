package com.nizam.wallset.data.repositories

import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.database.entities.Favorite
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

    fun getCategoryItems() = database.getWallPaperDao().getCategoryItems()

    fun getRecommendationWalls() = database.getWallPaperDao().getRecommendationWalls()

    fun getTopPicks() = database.getWallPaperDao().getTopPicks()

    fun getAllImages() = database.getWallPaperDao().getAllImages()

    suspend fun upsert(favorite: Favorite) = database.getFavoriteDao().upsert(favorite)

    suspend fun delete(favorite: Favorite) = database.getFavoriteDao().delete(favorite)

    fun getAllFavorites() = database.getFavoriteDao().getAllFavorites()

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
            }


            response.toString()
        } finally {
            urlConnection.disconnect()
        }
    }
}