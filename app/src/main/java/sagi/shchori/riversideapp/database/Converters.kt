package sagi.shchori.riversideapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import sagi.shchori.riversideapp.ui.models.Movie
import sagi.shchori.riversideapp.ui.models.MovieDetails
import sagi.shchori.riversideapp.ui.models.RatingItem

class Converters {

    @TypeConverter
    fun movieClassToString(movie: Movie): String {
        return Gson().toJson(movie)
    }

    @TypeConverter
    fun movieClassFromString(json: String): Movie {
        return Gson().fromJson(json, Movie::class.java)
    }

    @TypeConverter
    fun movieDetailsClassToString(details: MovieDetails?): String {
        return Gson().toJson(details ?: "")
    }

    @TypeConverter
    fun movieDetailsClassFromString(json: String): MovieDetails? {
        return try {
            Gson().fromJson(json, MovieDetails::class.java)
        } catch (ex: Exception) {
            null
        }
    }

    @TypeConverter
    fun ratingsItemClassToString(ratingItem: RatingItem): String {
        return Gson().toJson(ratingItem)
    }

    @TypeConverter
    fun ratingItemClassFromString(json: String): RatingItem {
        return Gson().fromJson(json, RatingItem::class.java)
    }
}