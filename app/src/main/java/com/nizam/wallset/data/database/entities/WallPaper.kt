package com.nizam.wallset.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WallPaper(
    val name: String,
    val owner: String,
    val category: String,
    @PrimaryKey
    val url: String
)