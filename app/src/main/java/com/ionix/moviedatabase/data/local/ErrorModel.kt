package com.ionix.moviedatabase.data.local

import com.google.gson.annotations.SerializedName

data class ErrorModel (
    @SerializedName("error")
    val error: String
)
