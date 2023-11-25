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

    @Query("SELECT category, MAX(url) as url FROM WallPaper GROUP BY category ORDER BY category")
    fun getCategoryItems(): LiveData<List<CategoryItem>>
}