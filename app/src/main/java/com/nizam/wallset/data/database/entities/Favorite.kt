package com.nizam.wallset.data.database.entities

import androidx.room.PrimaryKey

data class Favorite(
    @PrimaryKey
    val url: String,
    val lowResUrl: String,
)
