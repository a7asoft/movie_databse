package com.ionix.moviedatabase.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ionix.moviedatabase.R
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.databinding.ActivityMainBinding
import com.ionix.moviedatabase.ui.main.adapters.MoviesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listeners()
        setupRecyclerView()
        observe()
        initialData()
    }

    private fun initialData() {
        viewModel.getMovies()
    }

    private fun setupRecyclerView() {
        val mAdapter = MoviesAdapter(mutableListOf(), this)

        binding.rvPopular.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }
        binding.rvPopular.setHasFixedSize(true)
    }

    private fun observe() {
        observeState()
        observeMovies()
    }

    private fun observeMovies() {
        viewModel.mMovies
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { data ->
                handleMovies(data)
            }
            .launchIn(lifecycle.coroutineScope)
    }

    private fun handleMovies(data: MovieListResponseModel) {
        Log.wtf("DATA", "$data")

        //update movies
        binding.rvPopular.adapter?.let {
            if (it is MoviesAdapter) {
                it.updateList(data.items)
            }
        }
    }

    private fun observeState() {
        viewModel.mStateMovies
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(lifecycle.coroutineScope)
    }

    private fun handleState(state: MoviesState) {
        when (state) {
            is MoviesState.IsLoading -> {
                handleLoading(state.isLoading)
            }
            is MoviesState.ShowError -> {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG)
                    .show()
            }
            is MoviesState.Init -> {

            }
        }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading) {

        } else {

        }
    }

    private fun listeners() {

    }
}