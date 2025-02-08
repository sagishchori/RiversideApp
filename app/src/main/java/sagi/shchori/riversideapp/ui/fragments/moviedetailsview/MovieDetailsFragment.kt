package sagi.shchori.riversideapp.ui.fragments.moviedetailsview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import sagi.shchori.riversideapp.ui.viewmodels.MovieViewModel
import sagi.shchori.riversideapp.databinding.FragmentMovieDetailsBinding

// MovieDetailsFragment.kt

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedMovie.observe(viewLifecycleOwner) { movie ->
            binding.movieTitle.text = movie?.title
            binding.movieYear.text = movie?.year
            binding.moviePlot.text = movie?.movieDetails?.plot
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
