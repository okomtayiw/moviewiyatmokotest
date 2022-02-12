package com.testwiyatmoko.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.testwiyatmoko.R
import com.testwiyatmoko.data.network.NetworkResult
import com.testwiyatmoko.data.ResponseDetailMovie
import com.testwiyatmoko.databinding.FragmentDetailMovieBinding
import com.testwiyatmoko.until.NetworkListener
import com.testwiyatmoko.viewmodel.MovieDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailMovieFragment : Fragment() {

    private val viewModel: MovieDetailViewModel by viewModels()
    private var movieId = 0
    private var _binding: FragmentDetailMovieBinding? = null
    private val binding get() = _binding!!


    private lateinit var networkListener: NetworkListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailMovieBinding.inflate(inflater, container, false)


        arguments?.let {
            movieId = DetailMovieFragmentArgs.fromBundle(it).id
        }

        if (movieId != 0) {
            viewModel.getDetailMovie(movieId, "6bc90b57c5647cd87ee44270e4b52998")
            lifecycleScope.launchWhenStarted {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(requireContext())
                    .collect { status ->
                        Log.d("NetworkListener", status.toString())
                        viewModel.networkStatus = status
                        viewModel.showNetworkStatus()
                    }
            }
            observeViewModel()
        }


        return binding.root
    }


    private fun observeViewModel() {
        viewModel.responseDetailMovie.observe(viewLifecycleOwner) {
            loadData(it)
            Log.d("datane", "kosong")
        }
    }

    private fun loadData(dataResponse: NetworkResult<ResponseDetailMovie>?) {
        when (dataResponse) {
            is NetworkResult.Success -> {


                postValue(dataResponse.data)


            }
            is NetworkResult.Error -> {
                Toast.makeText(
                    requireContext(),
                    dataResponse.message.toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            is NetworkResult.Loading -> {

            }
        }

    }

    private fun postValue(results: ResponseDetailMovie?) {
        if (results != null) {
            binding.titleTextView.text = results.title
            binding.descTextView.text = results.overview
            val imageData = results.backdrop_path
            val glideOpt =
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).fitCenter()
                    .placeholder(
                        R.drawable.ic_image_bank
                    )
            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w300$imageData")
                .apply(glideOpt)
                .thumbnail(0.1f)
                .into(binding.mainImageView)
        }
    }


}