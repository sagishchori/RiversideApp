package sagi.shchori.riversideapp.ui.fragments.mainview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import sagi.shchori.riversideapp.R
import sagi.shchori.riversideapp.ui.models.Movie
import sagi.shchori.riversideapp.databinding.ItemMovieBinding
import javax.inject.Inject

class MovieAdapter @Inject constructor(private val listener: OnMovieClickListener) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = emptyList()

    private var selectedView: View? = null
    private var selectedPosition: Int = -1

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieYear.text = movie.year
            binding.favorite.setOnClickListener {
                movie.isFavorite = !movie.isFavorite

                setFavoriteImageResource(movie.isFavorite)

                listener.onFavoriteClicked(movie.imdbID, movie.isFavorite)
            }

            Glide.with(itemView.context)
                .load(movie.poster)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_image_not_supported_100)
                .into(binding.moviePoster)

            setFavoriteImageResource(movie.isFavorite)
        }

        private fun setFavoriteImageResource(favorite: Boolean) {
            if (favorite) {
                binding.favorite.setImageResource(R.drawable.ic_favorite_24)
            } else {
                binding.favorite.setImageResource(R.drawable.ic_favorite_border_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        setScaleToListItem(movie.isSelected, holder.itemView)

        // This is for first time after screen rotation
        if (movie.isSelected && selectedView == null) {
            selectedView = holder.itemView

            selectedPosition = position
        }

        holder.itemView.setOnClickListener {
            movie.isSelected = true

            setScaleToListItem(true, holder.itemView)

            // If the selected item is different than before it need to reset its appearance
            if (selectedView != it) {

                // Only if previous item exist scale it to normal
                selectedView?.let {     view ->
                    setScaleToListItem(false, view)

                    // reset the isSelected parameter to avoid UX issues
                    movies[selectedPosition].isSelected = false
                }

                selectedView = it

                selectedPosition = position
            }

            listener.onMovieClicked(movie, position)
        }
    }

    private fun setScaleToListItem(selected: Boolean, itemView: View) {
        if (selected) {
            itemView.scaleX = 1.2f
            itemView.scaleY = 1.2f
        } else {
            itemView.scaleX = 1f
            itemView.scaleY = 1f
        }
    }

    override fun getItemCount() = movies.size

    fun updateMovies(movies: List<Movie>?) {
        this.movies = movies ?: emptyList()

        notifyDataSetChanged()
    }
}