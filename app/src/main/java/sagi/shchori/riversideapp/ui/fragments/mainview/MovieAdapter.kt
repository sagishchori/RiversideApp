package sagi.shchori.riversideapp.ui.fragments.mainview

import android.view.LayoutInflater
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

    inner class MovieViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieYear.text = movie.year
            Glide.with(itemView.context)
                .load(movie.poster)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_image_not_supported_100)
                .into(binding.moviePoster)
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
        holder.itemView.scaleX = 1f
        holder.itemView.scaleY = 1f
        holder.itemView.setOnClickListener {
            it.scaleX = 1.2f
            it.scaleY = 1.2f
            listener.onMovieClicked(movie, position)
        }
    }

    override fun getItemCount() = movies.size

    fun updateMovies(movies: List<Movie>?) {
        this.movies = movies ?: emptyList()
        notifyDataSetChanged()
    }
}
