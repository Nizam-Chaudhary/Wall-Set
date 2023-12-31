package com.nizam.wallset.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nizam.wallset.data.database.entities.WallPaper

@Dao
interface WallPaperDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(wallPaper: WallPaper)

    @Query("Delete From wallpaper")
    suspend fun delete()

    @Query("SELECT url, lowResUrl FROM WallPaper WHERE category = :category")
    fun getWallPaperByCategories(category: String) : LiveData<List<ImageItem>>

    @Query("SELECT category, url, lowResUrl FROM WallPaper WHERE categoryDisplay = 1")
    fun getCategoryItems(): LiveData<List<CategoryItem>>

    @Query("SELECT url, lowResUrl FROM WallPaper ORDER BY RANDOM() LIMIT 5")
    fun getRecommendationWalls(): LiveData<List<ImageItem>>

    @Query("SELECT url, lowResUrl FROM WALLPAPER WHERE topPick = 1")
    fun getTopPicks() : LiveData<List<ImageItem>>

    @Query("SELECT url, lowResUrl FROM WallPaper")
    fun getAllImages() : LiveData<List<ImageItem>>
}