package com.example.myapplication.data

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mapapplicationkotlin.data.place.PlaceDao
import com.example.mapapplicationkotlin.data.place.PlaceEntity
import com.example.mapapplicationkotlin.data.user.UserDao
import com.example.mapapplicationkotlin.data.user.UserEntity

@Database(entities = [UserEntity::class, PlaceEntity::class], version = 1, exportSchema = false)
abstract class MyRoomDb : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var instance: MyRoomDb? = null

        fun getAppDataBase(context: Context): MyRoomDb {
            if (instance == null){
                instance = Room.databaseBuilder(
                    context,
                    MyRoomDb::class.java, "user_database")
                .fallbackToDestructiveMigration()
                .addCallback(roomCallBack)
                .allowMainThreadQueries()
                .build()
            }
            return instance as MyRoomDb
        }

        private val roomCallBack = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(instance!!).execute()
            }
        }
    }

    class PopulateDbAsync internal constructor(db: MyRoomDb) : AsyncTask<Void, Void, Void>() {
        private val mUserDao = db.userDao()
        private val mPlacerDao = db.placeDao()

        override fun doInBackground(vararg params: Void): Void? {
//             If we have no words, then create the initial list of words
            if (mUserDao.findAllUsers().isEmpty()){
                mUserDao.insert(UserEntity(0, "test@gmail.com", "admin", "123"))
            }

            if (mPlacerDao.findAllPlaces().isEmpty()) {
                mPlacerDao.insert(PlaceEntity(0, 1,"39.9286", "32.8547", "Ankara", "My City"))
            }
            return null
        }
    }
}