package com.example.mapapplicationkotlin.data.place

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.mapapplicationkotlin.data.relation_ships.UserWithPlaces
import com.example.myapplication.data.MyRoomDb

class PlaceViewModel(application: Application): AndroidViewModel(application) {

    private val placeRepository: PlaceRepository

    init {
        val placeDao = MyRoomDb.getAppDataBase(application).placeDao()
        placeRepository = PlaceRepository(placeDao)
    }

    fun insert(placeEntity: PlaceEntity) {
        placeRepository.insert(placeEntity)
    }

    fun delete(placeEntity: PlaceEntity) {
        placeRepository.delete(placeEntity)
    }

    fun getUserWithPlacesRoad(user_id: Long?): List<UserWithPlaces?>? {
        return placeRepository.getUserWithPlacesRoad(user_id)
    }

    fun getUserWithPlaces(id: Long): LiveData<List<UserWithPlaces>> {
//        viewModelScope.launch(Dispatchers.IO){
           return placeRepository.getUserWithPlaces(id)
//        }
    }
}