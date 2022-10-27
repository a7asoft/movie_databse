package com.ionix.moviedatabase.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.ionix.moviedatabase.data.remote.dto.Movie
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.databinding.ActivityMainBinding
import com.ionix.moviedatabase.ui.main.adapters.MoviesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var movieList = mutableListOf<Movie>()
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
        movieList = data.items as MutableList<Movie>
        //update movies
        binding.rvPopular.adapter?.let { it ->
            if (it is MoviesAdapter) {
                val ordered = data.items.sortedBy { getDate(it.releaseState) }.distinct().toList().reversed()
                it.updateList(ordered)
            }
        }
    }

    private fun getDate(releaseState: String): Date {
        val format = SimpleDateFormat("dd LLL yyyy", Locale.getDefault())
        var date = Date()
        try {
            date = format.parse(releaseState) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
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

        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // noop
            }

            override fun afterTextChanged(s: Editable) {
                val filterList =
                    movieList.filter { it.title.lowercase().contains(s.toString().lowercase()) }
                binding.rvPopular.adapter?.let {
                    if (it is MoviesAdapter) {
                        it.updateList(filterList)
                    }
                }
            }
        })
    }
}