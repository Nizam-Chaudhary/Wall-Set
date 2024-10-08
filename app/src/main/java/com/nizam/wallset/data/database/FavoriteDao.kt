package com.nizam.wallset.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.nizam.wallset.data.database.entities.Favorite

@Dao
interface FavoriteDao{
    @Upsert
    suspend fun upsert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("SELECT * FROM Favorite")
    fun getAllFavorites(): LiveData<List<ImageItem>>

    @Query("SELECT EXISTS(SELECT * FROM Favorite WHERE url = :url)")
    fun isExists(url: String): Boolean

    @Query("SELECT url FROM Favorite")
    fun getWallPaperForSlideShow(): Array<String>

    @Query("SELECT Count(*) FROM FAVORITE")
    fun getFavoritesCount(): Int
}