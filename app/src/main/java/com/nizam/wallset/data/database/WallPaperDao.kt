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

    @Query("SELECT * FROM WallPaper WHERE category = :category")
    fun getWallPaperByCategories(category: String) : LiveData<List<WallPaper>>

    @Query("SELECT category, url FROM WallPaper WHERE categoryDisplay = 1")
    fun getCategoryItems(): LiveData<List<CategoryItem>>

    @Query("SELECT url FROM WallPaper WHERE todayWall = 1")
    fun getTodayWall() : String

    @Query("SELECT url FROM WALLPAPER WHERE topPick = 1 ORDER BY RANDOM() LIMIT 4")
    fun getFourTopPicks() : MutableList<String>

    @Query("SELECT url FROM WALLPAPER WHERE topPick = 1 ORDER BY RANDOM()")
    fun getTopPicks() : LiveData<List<String>>

    @Query("SELECT url FROM WallPaper ORDER BY RANDOM()")
    fun getAllImages() : LiveData<List<String>>
}