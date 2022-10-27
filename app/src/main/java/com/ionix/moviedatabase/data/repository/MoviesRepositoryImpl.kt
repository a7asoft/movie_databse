package com.ionix.moviedatabase.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ionix.moviedatabase.data.local.ErrorModel
import com.ionix.moviedatabase.data.remote.api.MoviesApi
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.domain.common.BaseResult
import com.ionix.moviedatabase.domain.common.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(private val moviesApi: MoviesApi) :
    MoviesRepository {

    override suspend fun getMovies(key: String): Flow<BaseResult<MovieListResponseModel, ErrorModel>> {
        return flow {
            val response = moviesApi.getMovies(key)
            if (response.isSuccessful && response.code() == 200) {
                val body = response.body()!!
                emit(BaseResult.Success(body))
            } else {
                val type = object : TypeToken<ErrorModel>() {}.type
                val err = Gson().fromJson<ErrorModel>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                emit(BaseResult.Error(err))
            }
        }
    }
}