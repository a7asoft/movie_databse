package com.ionix.moviedatabase.ui.detail

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ionix.moviedatabase.R
import com.ionix.moviedatabase.data.remote.dto.Movie
import com.ionix.moviedatabase.data.remote.dto.MovieListResponseModel
import com.ionix.moviedatabase.databinding.ActivityMovieDetailBinding
import com.ionix.moviedatabase.ui.main.MainViewModel
import com.ionix.moviedatabase.ui.main.MoviesState
import com.ionix.moviedatabase.ui.main.adapters.MovieSuggestionsAdapter
import com.ionix.moviedatabase.utils.Functions
import com.ionix.moviedatabase.utils.Functions.Companion.makeStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@AndroidEntryPoint
class MovieDetailActivity : AppCompatActivity() {

    private lateinit var movie: Movie
    private lateinit var binding: ActivityMovieDetailBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        this.makeStatusBarTransparent()
        setContentView(binding.root)
        movie = intent.getSerializableExtra("MOVIE") as Movie
        setupRecyclerView()
        listeners()
        initialData()
        initConfig()
    }

    private fun listeners() {
        binding.btnRetry.setOnClickListener {
            observe()
            viewModel.getMovies()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initialData() {
        observe()
        viewModel.getMovies()
    }

    private fun observe() {
        observeState()
        observeMovies()
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
                binding.shimmer.visibility = View.GONE
                binding.rvSuggestions.visibility = View.GONE
                binding.tvSuggestions.visibility = View.GONE
            }
            is MoviesState.Init -> {

            }
        }
    }

    private fun handleLoading(loading: Boolean) {
        if (loading) {
            binding.shimmer.visibility = View.VISIBLE
            binding.rvSuggestions.visibility = View.GONE
            binding.errorLayout.visibility = View.GONE
            binding.tvSuggestions.visibility = View.GONE
        } else {
            binding.shimmer.visibility = View.GONE
            binding.rvSuggestions.visibility = View.VISIBLE
            binding.errorLayout.visibility = View.GONE
            binding.tvSuggestions.visibility = View.VISIBLE
        }
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
        //update movies
        binding.rvSuggestions.adapter?.let { it ->
            val array = movie.genres.split(",")
            if (it is MovieSuggestionsAdapter) {
                val filtered =
                    data.items.filter { m ->
                        movie.genreList.any { m.genres.contains(it.value) }
                    }.distinct()
                        .toList()
                Log.wtf("filtered", "$filtered")
                it.updateList(filtered)
            }
        }
    }

    private fun setupRecyclerView() {
        val mAdapter = MovieSuggestionsAdapter(mutableListOf(), this)

        binding.rvSuggestions.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(this@MovieDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvSuggestions.setHasFixedSize(true)
        mAdapter.setItemTapListener(object : MovieSuggestionsAdapter.OnItemTap {
            override fun onTap(movie: Movie) {
                val intent = Intent(this@MovieDetailActivity, MovieDetailActivity::class.java)
                intent.putExtra("MOVIE", movie)
                startActivity(intent)
            }
        })
    }

    private fun initConfig() {
        if (movie != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val url = URL(movie.image)
                try {
                    val image = BitmapFactory.decodeStream(withContext(Dispatchers.IO) {
                        withContext(Dispatchers.IO) {
                            url.openConnection()
                        }.getInputStream()
                    })
                    val bright = Functions.calculateBrightness(image, 1)

                    withContext(Dispatchers.Main) {
                        Blurry.with(this@MovieDetailActivity).from(image).into(binding.blurImage)

                        if (bright >= 120) {
                            binding.title.setTextColor(resources.getColor(R.color.black))
                            binding.genre.setTextColor(resources.getColor(R.color.black))
                        } else {
                            binding.title.setTextColor(resources.getColor(R.color.white))
                            binding.genre.setTextColor(resources.getColor(R.color.white))
                        }

                        binding.title.text = movie.title
                        binding.genre.text = movie.genres
                        if (movie.contentRating.isNotEmpty()) {
                            binding.contentRating.visibility = View.VISIBLE
                            binding.contentRating.text = movie.contentRating
                        } else {
                            binding.contentRating.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            Glide.with(this).load(movie.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.image)

            binding.sinopsis.text = movie.plot
            binding.stars.text = movie.stars
            binding.directors.text = movie.directors
            binding.release.text = movie.releaseState
            binding.imdbRating.text = movie.imDbRating
        }
    }
}