package com.ionix.moviedatabase.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieListResponseModel(
    @SerializedName("items")
    val items: List<Movie>,

    @SerializedName("errorMessage")
    val errorMessage: String
) : Serializable


data class Movie(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("fullTitle")
    val fullTitle: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("releaseState")
    val releaseState: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("runtimeMins")
    val runtimeMins: String,

    @SerializedName("runtimeStr")
    val runtimeStr: String,

    @SerializedName("plot")
    val plot: String,

    @SerializedName("contentRating")
    val contentRating: String,

    @SerializedName("imDbRating")
    val imDbRating: String,

    @SerializedName("imDbRatingCount")
    val imDbRatingCount: String,

    @SerializedName("metacriticRating")
    val metacriticRating: String,

    @SerializedName("genres")
    val genres: String,

    @SerializedName("genreList")
    val genreList: List<GenreList>,

    @SerializedName("directors")
    val directors: String,

    @SerializedName("directorList")
    val directorList: List<RList>,

    @SerializedName("stars")
    val stars: String,

    @SerializedName("starList")
    val starList: List<RList>
) : Serializable

data class RList(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
) : Serializable

data class GenreList(
    @SerializedName("key")
    val key: String,

    @SerializedName("value")
    val value: String
) : Serializable
