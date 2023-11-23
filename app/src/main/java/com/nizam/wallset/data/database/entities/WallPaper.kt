package com.nizam.wallset.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WallPaper(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val owner: String,
    val category: String,
    val url: String
)