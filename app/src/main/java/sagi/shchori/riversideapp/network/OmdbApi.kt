package sagi.shchori.riversideapp.network

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import sagi.shchori.riversideapp.ui.models.MovieDetails

interface OmdbApi {
    @GET("/")
    suspend fun searchMovies(@Query("apikey") apiKey: String, @Query("s") query: String): Response<MovieResponse>

    @GET("/")
    suspend fun getMovieDetails(@Query("apikey") apiKey: String, @Query("i") movieId: String): Response<MovieDetails>
}