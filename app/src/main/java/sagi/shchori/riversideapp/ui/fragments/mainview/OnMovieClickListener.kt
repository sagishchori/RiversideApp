package sagi.shchori.riversideapp.ui.fragments.mainview

import sagi.shchori.riversideapp.ui.models.Movie

interface OnMovieClickListener {
    fun onMovieClicked(movie: Movie, position: Int)
    fun onFavoriteClicked(movieId: String, isFavorite: Boolean)
}