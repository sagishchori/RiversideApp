package sagi.shchori.riversideapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sagi.shchori.riversideapp.ui.models.Movie

@Dao
interface MovieDao {

    @Insert
    @RewriteQueriesToDropUnusedColumns
    suspend fun insertMovie(movie: Movie)

    @Insert
    @RewriteQueriesToDropUnusedColumns
    suspend fun insertMovies(moviesList: List<Movie>)

    @Query("SELECT * FROM movies WHERE imdbID = :movieId")
    @RewriteQueriesToDropUnusedColumns
    suspend fun searchMovie(movieId: String): List<Movie>

    @Query("SELECT * FROM movies WHERE searchWord = :query")
    @RewriteQueriesToDropUnusedColumns
    suspend fun searchMovies(query: String): List<Movie>
}