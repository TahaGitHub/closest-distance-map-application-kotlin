package com.example.mapapplicationkotlin.data.place

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mapapplicationkotlin.data.relation_ships.UserWithPlaces

@Dao
interface PlaceDao {

    @Insert
    fun insert(placeEntity: PlaceEntity)

    @Delete
    fun delete(placeEntity: PlaceEntity)

    @Query("SELECT * FROM place_table")
    fun findAllPlaces(): List<PlaceEntity>

    @Query("SELECT * FROM place_table WHERE id = :id")
    fun findPlaceById(id: Long): PlaceEntity

    @Query("SELECT * FROM place_table WHERE place_name = :placename")
    fun findPlaceByUsername(placename: String): PlaceEntity

    @Query("SELECT * FROM user_table WHERE id = :user_id")
    fun getUserWithPlacesRoad(user_id: Long): List<UserWithPlaces>

    @Transaction
    @Query("SELECT * FROM user_table WHERE id = :user_id")
    fun getUserWithPlaceslists(user_id: Long): LiveData<List<UserWithPlaces>>
}