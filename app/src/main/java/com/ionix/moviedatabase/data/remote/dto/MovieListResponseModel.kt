package com.ionix.moviedatabase.data.remote.dto

data class MovieListResponseModel(
    val items: List<Movie>,
    val errorMessage: String
)

data class Movie(
    val id: String,
    val title: String,
    val fullTitle: String,
    val year: String,
    val releaseState: String,
    val image: String,
    val runtimeMins: String,
    val runtimeStr: String,
    val plot: String,
    val contentRating: ContentRating,
    val imDBRating: String,
    val imDBRatingCount: String,
    val metacriticRating: String,
    val genres: String,
    val genreList: List<GenreList>,
    val directors: String,
    val directorList: List<RList>,
    val stars: String,
    val starList: List<RList>
)

enum class ContentRating {
    Empty,
    NotRated,
    PG,
    PG13,
    R,
    Unrated
}

data class RList(
    val id: String,
    val name: String
)

data class GenreList(
    val key: String,
    val value: String
)
