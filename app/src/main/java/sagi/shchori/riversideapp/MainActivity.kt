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
            setReorderingAllowed(true)
            add(R.id.container, MainFragment())
            addToBackStack("MainFragment")
        }

        viewModel.selectedMovie.observe(this) { movie ->
            supportFragmentManager.commit {
                add(R.id.container, MovieDetailsFragment())
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
