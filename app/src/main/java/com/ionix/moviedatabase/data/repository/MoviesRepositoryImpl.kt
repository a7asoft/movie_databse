package com.ionix.moviedatabase.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ionix.moviedatabase.data.remote.api.MoviesApi
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.domain.common.BaseResult
import com.ionix.moviedatabase.domain.common.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(private val moviesApi: MoviesApi) :
    MoviesRepository {

    override suspend fun getMovies(key: String): Flow<BaseResult<MovieListResponseModel, MovieListResponseModel>> {
        return flow {
            val response = moviesApi.getMovies(key)
            if (response.isSuccessful) {
                val body = response.body()!!
                emit(BaseResult.Success(body))
            } else {
                val type = object : TypeToken<List<MovieListResponseModel>>() {}.type
                val err = Gson().fromJson<MovieListResponseModel>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                emit(BaseResult.Error(err))
            }
        }
    }
}