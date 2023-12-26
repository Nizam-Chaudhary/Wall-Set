package com.nizam.wallset.data.repositories

import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.database.entities.Favorite
import com.nizam.wallset.data.database.entities.WallPaper

class WallPaperRepository(
    private val database: WallPaperDatabase
) {
    suspend fun insert(wallPaper: WallPaper) = database.getWallPaperDao().insert(wallPaper)

    suspend fun delete() = database.getWallPaperDao().delete()

    fun getWallPaperByCategories(category: String) =
        database.getWallPaperDao().getWallPaperByCategories(category)

    fun getCategoryItems() = database.getWallPaperDao().getCategoryItems()

    fun getRecommendationWalls() = database.getWallPaperDao().getRecommendationWalls()

    fun getTopPicks() = database.getWallPaperDao().getTopPicks()

    fun getAllImages() = database.getWallPaperDao().getAllImages()

    suspend fun upsert(favorite: Favorite) = database.getFavoriteDao().upsert(favorite)

    suspend fun delete(favorite: Favorite) = database.getFavoriteDao().delete(favorite)

    fun getAllFavorites() = database.getFavoriteDao().getAllFavorites()

    fun isExists(url: String) = database.getFavoriteDao().isExists(url)

    fun getWallPaperForSlideShow() = database.getFavoriteDao().getWallPaperForSlideShow()
}