package sagi.shchori.riversideapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import sagi.shchori.riversideapp.databinding.ActivityMainBinding
import sagi.shchori.riversideapp.ui.UiState
import sagi.shchori.riversideapp.ui.fragments.mainview.MainFragment
import sagi.shchori.riversideapp.ui.fragments.moviedetailsview.MovieDetailsFragment
import sagi.shchori.riversideapp.ui.viewmodels.MovieViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MovieViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {

            // In case of a screen rotation the fragment should be in the backstack so no need to
            // set it again to the View
            supportFragmentManager.findFragmentByTag("MainFragment")?.let {
                return@commit
            }

            setReorderingAllowed(true)
            replace(R.id.container, MainFragment(), "MainFragment")
            addToBackStack("MainFragment")
        }

        viewModel.selectedMovie.observe(this) {

            supportFragmentManager.commit {

                // In case of a screen rotation the fragment should be in the backstack so no need to
                // set it again to the View
                supportFragmentManager.findFragmentByTag("DetailsFragment")?.let {
                    return@commit
                }

                // This is in the case of resetting the selected movie
                if (it == null) {
                    return@commit
                }

                add(R.id.container, MovieDetailsFragment(), "DetailsFragment")
                addToBackStack("DetailsFragment")
            }
        }

        viewModel.uiState.observe(this) {   state ->
            when(state) {
                is UiState.ERROR -> {
                    binding.progressbarContainer.visibility = View.GONE

                    showDialog("Error", state.error.toString())
                }

                UiState.IDLE -> {
                    binding.progressbarContainer.visibility = View.GONE
                }

                UiState.LOADING -> {
                    binding.progressbarContainer.visibility = View.VISIBLE
                }
            }
        }

        registerForBackPressToFinishActivity()
    }

    private fun registerForBackPressToFinishActivity() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.backStackEntryCount > 1) {
                        supportFragmentManager.popBackStack()

                        // This will reset the selection each time the user goes out to the list
                        viewModel.selectMovie(null)
                    } else {
                        finish()
                    }
                }
            })
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok", null)
            .show()
    }
}
