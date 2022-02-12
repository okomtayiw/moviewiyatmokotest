package com.testwiyatmoko.repository

import com.testwiyatmoko.data.ResponseDetailMovie
import com.testwiyatmoko.data.ResponseMovie
import com.testwiyatmoko.data.network.MovieApiService
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val movieApiService: MovieApiService
) {
    suspend fun getMoviePlaying(page: Int, apiKey: String): Response<ResponseMovie> {
        return movieApiService.getMoviePlaying(page, apiKey)
    }

    suspend fun getMovieDetail(movieId: Int, apiKey: String): Response<ResponseDetailMovie> {
        return movieApiService.getMovieDetail(movieId, apiKey)
    }

    suspend fun getMovieSearch(query: String, apiKey: String): Response<ResponseMovie> {
        return movieApiService.getMovieSearch(query, apiKey)
    }
}

