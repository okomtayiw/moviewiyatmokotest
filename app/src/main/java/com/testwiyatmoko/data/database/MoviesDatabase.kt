package com.testwiyatmoko.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.testwiyatmoko.data.database.entity.MoviesEntity

@Database(
    entities = [MoviesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MoviesTypeConverter::class)
abstract class MoviesDatabase: RoomDatabase() {

    abstract fun moviesDao():MoviesDao
}