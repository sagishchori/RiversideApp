package sagi.shchori.riversideapp.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import sagi.shchori.riversideapp.database.MovieDao
import sagi.shchori.riversideapp.network.MovieResponse
import sagi.shchori.riversideapp.network.OmdbApi
import sagi.shchori.riversideapp.network.Result
import sagi.shchori.riversideapp.ui.models.Movie
import sagi.shchori.riversideapp.ui.models.MovieDetails
import java.net.UnknownHostException
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
        val response = try {
            api.searchMovies("6b673588", query)
        } catch (ex: Exception) {
            if (dbResult.isNotEmpty()) {

                // The network call failed but the DB returned a list
                return@flow
            } else {

                // Both the network and DB calls failed to fetch any data -> return a network error
                emit(Result.Error(Exception(ex.message)))
            }

            null
        }

        if (response?.isSuccessful == true) {
            response.body()?.let {

                it.search?.let {    list ->
                    list.forEach {  movie ->
                        movie.searchWord = query
                    }

                    // If the result is successful need to save the result to DB
                    movieDao.insertMovies(it.search)
                }

                // Emit and update the ui accordingly
                emit(Result.Success(it))
            }
        } else {
            response?.errorBody()?.let {
                emit(Result.Error(Exception(it.string())))
            }
        }
    }

    /**
     * Search for more details about a specific movie
     *
     * @param movie     The movie id to search for more details
     */
    suspend fun movieDetails(movie: Movie): Flow<Result<MovieDetails>> = flow {
        emit(Result.Loading)

        // First, check in the DB for the movie according to movie id
        val dbResult = movieDao.searchMovie(movie.imdbID)
        val movieFromDB = dbResult[0]
        movieFromDB.movieDetails?.let {
            emit(Result.Success(it))
        }

        val response = try {
            api.getMovieDetails("6b673588", movie.imdbID)
        } catch (ex: Exception) {

            if (dbResult.isNotEmpty() && movieFromDB.movieDetails != null) {

                // In case the network call failed but there is a result from DB and its movie
                // details object is not null since this is the data to show
                return@flow
            } else if (dbResult.isNotEmpty() && movieFromDB.movieDetails == null) {

                // In case the network call failed but there is a result from DB. The DB returned a
                // movie but with no movie details i.e. first time loading the movie details -> send
                // an Error result
                emit(Result.Error(Exception("Trying to fetch data for ${movieFromDB.title} failed.")))
            } else {

                // When both network and DB calls failed -> emit the exception and return null as
                // response
                emit(Result.Error(ex))
            }

            null
        }

        if (response?.isSuccessful == true) {
            response.body()?.let {

                // Update the movie details from the web
                movieFromDB?.movieDetails = it

                // If the result is successful save the movie to DB
                movieDao.insertMovie(movieFromDB)

                // Emit and update the ui accordingly
                emit(Result.Success(it))
            }
        } else {
            response?.errorBody()?.let {
                emit(Result.Error(Exception(it.string())))
            }
        }
    }
}