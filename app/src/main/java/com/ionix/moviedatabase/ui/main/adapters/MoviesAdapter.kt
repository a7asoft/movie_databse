package com.ionix.moviedatabase.ui.main.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ionix.moviedatabase.data.remote.dto.Movie
import com.ionix.moviedatabase.databinding.ItemMovieBinding

class MoviesAdapter(private val docs: MutableList<Movie>, private val context: Context) :
    RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    fun updateList(mPosts: List<Movie>) {
        docs.clear()
        docs.addAll(mPosts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(movie: Movie) {
            itemBinding.tvMovieTitle.text = movie.title
            itemBinding.tvMovieTitle.isSelected = true
            itemBinding.tvImdb.text = movie.imDbRating
            if (movie.contentRating != "") {
                itemBinding.tvContentRating.text = movie.contentRating
            } else {
                itemBinding.linearContentRating.visibility = View.GONE
            }
            Glide.with(context).load(movie.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(itemBinding.ivMoviePoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rv_item_anim)
        return holder.bind(docs[position])
    }

    override fun getItemCount() = docs.size

}