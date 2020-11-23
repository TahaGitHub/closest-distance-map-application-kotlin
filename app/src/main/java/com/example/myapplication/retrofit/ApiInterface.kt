package com.example.myapplication.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("driving-car")
    fun getRoadDuration(
        @Query("api_key") authHeader: String?,
        @Query("start") start: String?,
        @Query("end") end: String?
    ): Call<JsonObject>
}