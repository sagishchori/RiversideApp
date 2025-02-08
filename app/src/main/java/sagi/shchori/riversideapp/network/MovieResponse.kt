package sagi.shchori.riversideapp.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import sagi.shchori.riversideapp.ui.models.Movie

data class MovieResponse(
    @SerializedName("Search") val search: List<Movie>,
    val totalResults: String,
    @SerializedName("Response") val response: String
)
