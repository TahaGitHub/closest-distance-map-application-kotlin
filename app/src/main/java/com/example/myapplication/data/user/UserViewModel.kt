package com.example.mapapplicationkotlin.data.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.MyRoomDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository: UserRepository

    init {
        val userDao = MyRoomDb.getAppDataBase(application).userDao()
        userRepository = UserRepository(userDao)
    }

    fun insert(userEntity: UserEntity){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insert(userEntity)
        }
    }

    fun findUserById(id: Long): UserEntity {
          return userRepository.findUserById(id)
    }

    fun findUserByUsername(userName: String): UserEntity {
        return userRepository.findUserByUsername(userName)
    }

    fun findUserByUsernameAndPassword(userName: String, password: String): UserEntity {
        return userRepository.findUserByUsernameAndPassword(userName, password)
    }
}