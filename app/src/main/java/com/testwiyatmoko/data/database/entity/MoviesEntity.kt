package com.testwiyatmoko.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.testwiyatmoko.data.ResponseMovie
import com.testwiyatmoko.until.Constans

@Entity(tableName = Constans.MOVIES_TABLE)
class MoviesEntity(
    var responseMovie: ResponseMovie
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}