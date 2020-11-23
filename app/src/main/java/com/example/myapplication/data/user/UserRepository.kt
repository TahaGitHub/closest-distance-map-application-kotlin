package com.example.mapapplicationkotlin.data.user

class UserRepository (private val userDao: UserDao) {

    fun insert(userEntity: UserEntity){
        userDao.insert(userEntity)
    }

    fun findUserById(id: Long): UserEntity {
        return userDao.findUserById(id)
    }

    fun findUserByUsername(userName: String): UserEntity {
        return userDao.findUserByUsername(userName)
    }

    fun findUserByUsernameAndPassword(userName: String, password: String): UserEntity{
        return userDao.findUserByUsernameAndPassword(userName, password)
    }
}