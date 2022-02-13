package com.testwiyatmoko.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.testwiyatmoko.data.ResponseMovie
import com.testwiyatmoko.data.Result

class MoviesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun moviesToString(responseMovie: ResponseMovie): String {
        return gson.toJson(responseMovie)
    }

    @TypeConverter
    fun stringToMovies(data: String): ResponseMovie {
        val listType = object : TypeToken<ResponseMovie>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resultToString(result: com.testwiyatmoko.data.Result): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data: String): com.testwiyatmoko.data.Result {
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(data, listType)
    }
}