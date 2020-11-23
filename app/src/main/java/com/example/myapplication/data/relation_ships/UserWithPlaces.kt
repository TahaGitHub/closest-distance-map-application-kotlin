package com.example.mapapplicationkotlin.data.relation_ships

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mapapplicationkotlin.data.place.PlaceEntity
import com.example.mapapplicationkotlin.data.user.UserEntity

data class UserWithPlaces (

    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val placesEntities: List<PlaceEntity>
)