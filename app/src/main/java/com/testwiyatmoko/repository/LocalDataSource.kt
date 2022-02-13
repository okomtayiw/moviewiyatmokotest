package com.testwiyatmoko.repository

import com.testwiyatmoko.data.database.MoviesDao
import com.testwiyatmoko.data.database.entity.MoviesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val moviesDao: MoviesDao
) {
    fun readMovies(): Flow<List<MoviesEntity>> {
        return moviesDao.readMovies()
    }

    suspend fun insertMovies(moviesEntity: MoviesEntity) {
        moviesDao.insertMovies(moviesEntity)
    }



}