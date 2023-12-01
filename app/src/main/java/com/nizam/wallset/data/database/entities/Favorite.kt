package com.nizam.wallset.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Favorite(
    @PrimaryKey
    val url: String,
    val lowResUrl: String,
)
