package sagi.shchori.riversideapp.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sagi.shchori.riversideapp.database.MovieDao
import sagi.shchori.riversideapp.network.MovieResponse
import sagi.shchori.riversideapp.network.OmdbApi
import sagi.shchori.riversideapp.network.Result
import sagi.shchori.riversideapp.ui.models.Movie
import sagi.shchori.riversideapp.ui.models.MovieDetails
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: OmdbApi,
    private val movieDao: MovieDao
) {

    /**
     * Search for a movie according to user input and return the result when ready
     *
     * @param query     The input of the user to search for
     */
    suspend fun searchMovies(query: String): Flow<Result<MovieResponse>> = flow {
        emit(Result.Loading)

        // First, check in the DB for the search word
        val dbResult = movieDao.searchMovies(query)
        if (dbResult.isNotEmpty()) {

            // The DB is filled with movies save for the search word
            emit(Result.Success(MovieResponse(dbResult, "", "")))
        }

        // continue to search on the web
        val response = api.searchMovies("6b673588", query)
        if (response.isSuccessful) {
            response.body()?.let {

                it.search.forEach {     movie ->
                    movie.searchWord = query
                }
                // If the result is successful need to save the result to DB
                movieDao.insertMovies(it.search)

                // Emit and update the ui accordingly
                emit(Result.Success(it))
            }
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(it.string())))
            }
        }
    }

    /**
     * Search for more details about a specific movie
     *
     * @param movieId   The movie id to search for more details
     */
    suspend fun movieDetails(movie: Movie): Flow<Result<MovieDetails>> = flow {
        emit(Result.Loading)

        // First, check in the DB for the movie according to movie id
        val dbResult = movieDao.searchMovie(movie.imdbID)
        dbResult[0].movieDetails?.let {
            emit(Result.Success(it))
        }

        val response = api.getMovieDetails("6b673588", movie.imdbID)
        if (response.isSuccessful) {
            response.body()?.let {

                // Update the movie details from the web
                movie.movieDetails = it

                // If the result is successful save the movie to DB
                movieDao.insertMovie(movie)

                // Emit and update the ui accordingly
                emit(Result.Success(it))
            }
        } else {
            response.errorBody()?.let {
                emit(Result.Error(Exception(it.string())))
            }
        }
    }
}