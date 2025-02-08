package sagi.shchori.riversideapp.ui.models

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @ColumnInfo("title")
    @SerializedName("Title") val title: String,
    @ColumnInfo("year")
    @SerializedName("Year") val year: String,
    @ColumnInfo("imdbID")
    @SerializedName("imdbID") val imdbID: String,
    @ColumnInfo("type")
    @SerializedName("Type") val type: String,
    @ColumnInfo("poster")
    @SerializedName("Poster") val poster: String
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Int = 0

    @ColumnInfo("searchWord")
    var searchWord: String = ""

    @ColumnInfo("movieDetails")
    @Nullable
    var movieDetails: MovieDetails? = null

    @ColumnInfo("isFavorite")
    var isFavorite: Boolean = false
}