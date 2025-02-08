package sagi.shchori.riversideapp.ui.fragments.mainview

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import sagi.shchori.riversideapp.ui.models.Movie
import sagi.shchori.riversideapp.ui.viewmodels.MovieViewModel
import sagi.shchori.riversideapp.databinding.FragmentMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), OnMovieClickListener {

    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var adapter: MovieAdapter
    private var selectedItem = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput: EditText = binding.searchInput
        val recyclerView: RecyclerView = binding.recyclerView

        recyclerView.adapter = adapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.onFlingListener = snapHelper

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchMovies(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.movies.observe(viewLifecycleOwner) { movies ->

            // Update the list anyway. When the clears his text field the list should be updated
            // even with no results to clear the previous search results
            adapter.updateMovies(movies)

            // In case of no results -> show error to text field
            if (movies == null) {
                binding.searchInput.error = "No results yet"
            } else {

                // Remove the error from text field
                binding.searchInput.error = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMovieClicked(movie: Movie, position: Int) {
        if (selectedItem > -1) {
            binding.recyclerView[selectedItem].let {
                it.scaleX = 1f
                it.scaleY = 1f
            }
        }

        selectedItem = position

        viewModel.selectMovie(movie)
    }

    override fun onFavoriteClicked(movieId: String, isFavorite: Boolean) {
        viewModel.setMovieAsFavorite(movieId, isFavorite)
    }
}