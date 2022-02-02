package ru.netology.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.dao.PostDao
import ru.netology.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}