package com.ionix.moviedatabase.domain.movies

import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.domain.common.BaseResult
import com.ionix.moviedatabase.domain.common.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(private val moviesRepository: MoviesRepository) {
    suspend fun invoke(key: String): Flow<BaseResult<MovieListResponseModel, MovieListResponseModel>> {
        return moviesRepository.getMovies(key)
    }
}