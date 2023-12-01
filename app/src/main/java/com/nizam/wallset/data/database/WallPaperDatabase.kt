package com.nizam.wallset.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nizam.wallset.data.database.entities.Favorite
import com.nizam.wallset.data.database.entities.WallPaper

@Database(
    entities = [WallPaper::class, Favorite::class],
    version = 1
)
abstract class WallPaperDatabase : RoomDatabase() {
    abstract fun getWallPaperDao() : WallPaperDao

    companion object {
        @Volatile
        private var instance: WallPaperDatabase? = null

        private var LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            WallPaperDatabase::class.java,
            name = "WallSetDb.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}