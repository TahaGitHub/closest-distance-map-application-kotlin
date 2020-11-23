package com.example.mapapplicationkotlin.data.road_diraction

import androidx.room.Embedded
import com.example.mapapplicationkotlin.data.place.PlaceEntity
import com.google.gson.JsonArray

class RoadDiraction(
    @Embedded val Road_placeEntity: PlaceEntity?,
    val distance: Float?,
    val road_Points: JsonArray?
)