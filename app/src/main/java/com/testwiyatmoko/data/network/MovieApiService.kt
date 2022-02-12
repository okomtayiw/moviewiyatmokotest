package com.testwiyatmoko.data.network

import com.testwiyatmoko.data.ResponseDetailMovie
import com.testwiyatmoko.data.ResponseMovie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("/3/movie/now_playing")
    suspend fun getMoviePlaying(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Response<ResponseMovie>

    @GET("/3/movie/{movieId}")
    suspend fun getMovieDetail(@Path("movieId") movieId: Int, @Query("api_key") apiKey: String
    ): Response<ResponseDetailMovie>

    @GET("3/search/movie")
    suspend fun getMovieSearch(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): Response<ResponseMovie>
}