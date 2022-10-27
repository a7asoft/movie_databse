package com.ionix.moviedatabase.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.ionix.moviedatabase.R
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
        binding.swipeLayout.isRefreshing = false
        movieList = data.items as MutableList<Movie>
        //update movies
        binding.rvPopular.adapter?.let { it ->
            if (it is MoviesAdapter) {
                val ordered =
                    data.items.sortedBy { getDate(it.releaseState) }.distinct().toList().reversed()
                it.updateList(ordered)
            }
        }

        var listGenres = mutableListOf<String>()
        //resolving genres
        data.items.forEach { movie ->
            movie.genreList.forEach { genre ->
                listGenres.add(genre.value)
            }
        }

        listGenres = listGenres.distinct().toMutableList()
        //building chips dynamically
        listGenres.forEach { genreString ->
            val chip = layoutInflater.inflate(R.layout.chip_filter, null, false) as Chip
            chip.text = genreString
            val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
            ).toInt()
            chip.setPadding(paddingDp, 0, paddingDp, 0)
            chip.setOnCheckedChangeListener { b, c ->
                if (c) {
                    val filterList =
                        movieList.filter {
                            it.genres.lowercase().contains(b.text.toString().lowercase())
                        }
                    binding.rvPopular.adapter?.let { rvA ->
                        if (rvA is MoviesAdapter) {
                            rvA.updateList(filterList.sortedBy { getDate(it.releaseState) }
                                .distinct().toList().reversed())
                        }
                    }
                } else {
                    binding.rvPopular.adapter?.let { rvA ->
                        if (rvA is MoviesAdapter) {
                            rvA.updateList(movieList.sortedBy { getDate(it.releaseState) }
                                .distinct().toList().reversed())
                        }
                    }
                }

            }
            binding.chipGenres.addView(chip)
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
                binding.errorLayout.visibility = View.VISIBLE
                binding.searchContainer.visibility = View.GONE
                binding.textPopular.visibility = View.GONE
                binding.rvPopular.visibility = View.GONE
                binding.chipGenres.visibility = View.GONE
                binding.errorMessage.text = state.message
                binding.swipeLayout.visibility = View.GONE
                binding.swipeLayout.isRefreshing = false
            }
            is MoviesState.Init -> {

            }
        }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading) {
            binding.shimmer.visibility = View.VISIBLE
            binding.errorLayout.visibility = View.GONE
            binding.chipGenres.visibility = View.GONE
            binding.textPopular.visibility = View.GONE
            binding.rvPopular.visibility = View.GONE
        } else {
            binding.shimmer.visibility = View.GONE
            binding.searchContainer.visibility = View.VISIBLE
            binding.chipGenres.visibility = View.VISIBLE
            binding.textPopular.visibility = View.VISIBLE
            binding.rvPopular.visibility = View.VISIBLE
            binding.swipeLayout.visibility = View.VISIBLE
        }
    }

    private fun listeners() {
        binding.btnRetry.setOnClickListener {
            Log.wtf("btnRetry", "Clicked!")
            observe()
            viewModel.getMovies()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // noop
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    val filterList =
                        movieList.filter { it.title.lowercase().contains(s.toString().lowercase()) }
                    binding.rvPopular.adapter?.let {
                        if (it is MoviesAdapter) {
                            it.updateList(filterList.sortedBy { getDate(it.releaseState) }
                                .distinct().toList().reversed())
                        }
                    }
                } else {
                    binding.rvPopular.adapter?.let { rvA ->
                        if (rvA is MoviesAdapter) {
                            rvA.updateList(movieList.sortedBy { getDate(it.releaseState) }
                                .distinct().toList().reversed())
                        }
                    }
                }

            }
        })

        binding.swipeLayout.setOnRefreshListener {
            observe()
            viewModel.getMovies()
        }
    }
}