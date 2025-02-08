package sagi.shchori.riversideapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sagi.shchori.riversideapp.ui.models.Movie

@Dao
@RewriteQueriesToDropUnusedColumns
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(moviesList: List<Movie>)

    @Query("SELECT * FROM movies WHERE imdbID = :movieId")
    suspend fun searchMovie(movieId: String): List<Movie>

    @Query("SELECT * FROM movies WHERE searchWord = :query")
    suspend fun searchMovies(query: String): List<Movie>
}