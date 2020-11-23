package com.example.mapapplicationkotlin.data.place

import androidx.lifecycle.LiveData
import com.example.mapapplicationkotlin.data.relation_ships.UserWithPlaces

class PlaceRepository(private val placeDao: PlaceDao) {

    fun insert(placeEntity: PlaceEntity) {
        placeDao.insert(placeEntity)
    }

    fun delete(placeEntity: PlaceEntity){
        placeDao.delete(placeEntity)
    }

    fun getUserWithPlacesRoad(user_id: Long?): List<UserWithPlaces?>? {
        return placeDao.getUserWithPlacesRoad(user_id!!)
    }

    fun getUserWithPlaces(id: Long): LiveData<List<UserWithPlaces>> {
        return placeDao.getUserWithPlaceslists(id)
    }
}