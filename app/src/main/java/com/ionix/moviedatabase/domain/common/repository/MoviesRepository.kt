package com.ionix.moviedatabase.domain.common.repository

import com.ionix.moviedatabase.data.local.ErrorModel
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.domain.common.BaseResult
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getMovies(key: String): Flow<BaseResult<MovieListResponseModel, ErrorModel>>
}