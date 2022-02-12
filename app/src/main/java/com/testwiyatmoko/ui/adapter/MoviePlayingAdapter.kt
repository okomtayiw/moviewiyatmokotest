package com.testwiyatmoko.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.testwiyatmoko.R
import com.testwiyatmoko.data.Result
import javax.inject.Inject

class MoviePlayingAdapter @Inject constructor(
    val context: Context,
    private val movieList: List<Result>
) : RecyclerView.Adapter<MoviePlayingAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int, model: Result)

    }


    fun setOnclicklistener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.title_textView)
        val overView : TextView = view.findViewById(R.id.description_textView)
        val imageView: ImageView = view.findViewById(R.id.movie_imageView)
        val popularity: TextView = view.findViewById(R.id.popularity_textView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_item_movie, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageData = movieList[position].backdrop_path
        val glideOpt = RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).fitCenter().placeholder(R.drawable.ic_image_bank)
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w300$imageData")
            .apply(glideOpt)
            .thumbnail(0.1f)
            .into(holder.imageView)
        holder.textViewTitle.text = movieList[position].title
        holder.overView.text = movieList[position].overview
        holder.popularity.text = movieList[position].popularity.toString()
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, movieList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}