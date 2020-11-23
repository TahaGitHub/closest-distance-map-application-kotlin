package com.example.mapapplicationkotlin.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val email: String,
    val username: String,
    val password: String
)