package sagi.shchori.riversideapp.network

/**
 * A class to represent the result of an IO call either to a network or to a DataBase e.g. Room
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val exception: Exception) : Result<Nothing>()

    data object Loading : Result<Nothing>()
}
