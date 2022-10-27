package com.ionix.moviedatabase.data.remote.api

import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface MoviesApi {
    //Get movies
    @Headers("Content-Type: application/json")
    @GET("movies.json")
    suspend fun getMovies(
        @Query("key") key: String
    ): Response<MovieListResponseModel>
}