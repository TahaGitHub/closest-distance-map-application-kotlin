package com.example.mapapplicationkotlin.data.place

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_table")
data class PlaceEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val user_id: Long,

    val position1: String,
    val position2: String,

    val place_name: String,
    val place_description: String
)