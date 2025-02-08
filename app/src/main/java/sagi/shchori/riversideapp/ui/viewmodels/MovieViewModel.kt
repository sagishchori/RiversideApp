package sagi.shchori.riversideapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val _selectedPosition = MutableLiveData<Int>(-1)
    val selectedPosition: LiveData<Int> get() = _selectedPosition

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
                    }

                    Result.Loading -> {
                        _uiState.postValue(UiState.LOADING)
                    }

                    is Result.Success -> {
                        _uiState.postValue(UiState.IDLE)

                        if (_selectedPosition.value!! > -1) {
                            result.data.search[_selectedPosition.value!!].isSelected = true
                        }

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
        movie?.let {
            viewModelScope.launch(Dispatchers.IO) {

                // It is not mandatory, it just allow the user to experience the snapping to center
                delay(1000)

                // This will ensure loading the movie data first and then transfer the user to
                // MovieDetailsFragment
                movieDetails(movie!!)
            }
        }

        // This resets the selected movie when a user go back to movies list
        _selectedMovie.value = null
    }

    /**
     * This function is being called from the main view since the user sets a movie as favorite from
     * the list. It also creates separation from movie details view.
     */
    fun setMovieAsFavorite(movieId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setMovieAsFavorite(movieId, isFavorite)
        }
    }

    /**
     * This function is being called from the movie details view because when a user enters this
     * view and click on favorite he adds it to its favorite list. It also creates a separation from
     * setting favorite from the main view.
     */
    fun addOrRemoveMovieAsFavorite() {
        viewModelScope.launch {

            _selectedMovie.value?.let { selectedMovie ->

                _movies.value?.forEach {    movieFromList ->

                    // Search for the selected movie in the movie list to change the isFavorite
                    // value
                    if (movieFromList.imdbID == selectedMovie.imdbID) {
                        movieFromList.isFavorite = !selectedMovie.isFavorite

                        _movies.postValue(_movies.value)

                        // Once it was found -> break
                        return@forEach
                    }
                }

                repository.setMovieAsFavorite(selectedMovie.imdbID, !selectedMovie.isFavorite)
            }
        }
    }

    /**
     * Set the RecyclerView's position of the selected item
     */
    fun setSelectedPosition(position: Int) {
        _selectedPosition.postValue(position)
    }
}