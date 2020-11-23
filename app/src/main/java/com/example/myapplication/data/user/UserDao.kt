package com.example.mapapplicationkotlin.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(userEntity: UserEntity)

    @Query("SELECT * FROM user_table")
    fun findAllUsers(): List<UserEntity>

    @Query("SELECT * FROM user_table WHERE username = :username and password = :password")
    fun findUserByUsernameAndPassword(username: String, password: String): UserEntity

    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    fun findUserById(id: Long?): UserEntity

    @Query("SELECT * FROM user_table WHERE username = :username")
    fun findUserByUsername(username: String): UserEntity
}