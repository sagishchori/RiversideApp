package sagi.shchori.riversideapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sagi.shchori.riversideapp.network.Result
import sagi.shchori.riversideapp.repositories.MovieRepository
import sagi.shchori.riversideapp.ui.UiState
import sagi.shchori.riversideapp.ui.models.Movie
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies

    private val _selectedMovie = MutableLiveData<Movie>()
    val selectedMovie: LiveData<Movie> get() = _selectedMovie

    private val _uiState = MutableLiveData<UiState<*>>()
    val uiState: LiveData<UiState<*>> = _uiState

    /**
     * Search for a movie according to user input
     *
     * @param query     The user input
     */
    fun searchMovies(query: String) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            when(exception) {
                is Exception -> {
                    _uiState.postValue(UiState.ERROR(exception.message))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            _uiState.postValue(UiState.LOADING)

            repository.searchMovies(query).collect {    result ->
                when(result) {
                    is Result.Error -> {
                        _uiState.postValue(UiState.ERROR(result.exception.message))
//                        "Something went wrong when trying to load the movies list"
                    }

                    Result.Loading -> {
                        _uiState.postValue(UiState.LOADING)
                    }

                    is Result.Success -> {
                        _uiState.postValue(UiState.IDLE)

                        _movies.postValue(result.data.search)
                    }
                }
            }
        }
    }

    /**
     * Search for a specific movie details
     *
     * @param movie   The movie received from the user's clicked item on the [MainFragment]
     */
    private suspend fun movieDetails(movie: Movie) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            when(exception) {
                is Exception -> {
                    _uiState.postValue(UiState.ERROR(exception.message))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            _uiState.postValue(UiState.LOADING)

            // Get more details by using the movie id to get more data for a specific movie
            repository.movieDetails(movie).collect { result ->
                when(result) {
                    is Result.Error -> {
                        _uiState.postValue(UiState.ERROR(result.exception.message))
                    }

                    Result.Loading -> {
                        _uiState.postValue(UiState.LOADING)
                    }

                    is Result.Success -> {
                        movie.movieDetails = result.data

                        _selectedMovie.postValue(movie)

                        _uiState.postValue(UiState.IDLE)
                    }
                }
            }
        }
    }

    fun selectMovie(movie: Movie?) {
        if (movie == null) {
            _selectedMovie.value = null
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            // This will ensure loading the movie data first and then transfer the user to
            // MovieDetailsFragment
            movieDetails(movie!!)
        }
    }
}
