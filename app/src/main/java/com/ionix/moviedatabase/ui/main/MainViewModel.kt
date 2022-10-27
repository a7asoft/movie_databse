package com.ionix.moviedatabase.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionix.moviedatabase.BuildConfig
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.domain.common.BaseResult
import com.ionix.moviedatabase.domain.movies.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    //state get movies
    private val stateMovies = MutableStateFlow<MoviesState>(
        MoviesState.Init
    )
    val mStateMovies: StateFlow<MoviesState> get() = stateMovies

    //observable movie list
    private val _movies = MutableSharedFlow<MovieListResponseModel>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val mMovies: Flow<MovieListResponseModel> get() = _movies.distinctUntilChanged()

    private fun setLoading() {
        stateMovies.value = MoviesState.IsLoading(true)
    }

    private fun hideLoading() {
        stateMovies.value = MoviesState.IsLoading(false)
    }

    private fun showError(message: String) {
        stateMovies.value = MoviesState.ShowError(message)
    }

    fun getMovies() {
        viewModelScope.launch {
            getMoviesUseCase.invoke(BuildConfig.KEY)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showError(exception.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            _movies.tryEmit(result.data)
                        }
                        is BaseResult.Error -> {
                            showError(result.rawResponse.error)
                            Log.wtf("BaseResult.Error","${result.rawResponse}")
                        }
                    }
                }
        }
    }
}


sealed class MoviesState {
    object Init : MoviesState()
    data class IsLoading(val isLoading: Boolean) : MoviesState()
    data class ShowError(val message: String) : MoviesState()
}