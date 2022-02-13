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
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.testwiyatmoko.data.ResponseMovie
import com.testwiyatmoko.data.Result
import com.testwiyatmoko.data.network.NetworkResult
import com.testwiyatmoko.databinding.FragmentListMovieBinding
import com.testwiyatmoko.ui.adapter.MoviePlayingAdapter
import com.testwiyatmoko.until.NetworkListener
import com.testwiyatmoko.viewmodel.PlayingNowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListMovieFragment : Fragment() {

    private lateinit var adapter: MoviePlayingAdapter

    private val viewModel: PlayingNowViewModel by viewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var networkListener: NetworkListener
    private var _binding: FragmentListMovieBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListMovieBinding.inflate(inflater, container, false)

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    viewModel.networkStatus = status
                    viewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        binding.toSearch.setOnClickListener {
            gotoSearchMovie()
        }

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.responseMovie.observe(viewLifecycleOwner) {
            loadData(it)

        }
    }

    private fun loadData(dataResponse: NetworkResult<ResponseMovie>?) {
        when (dataResponse) {
            is NetworkResult.Success -> {
                binding.progressView.visibility = View.GONE
                if (dataResponse.data?.results!!.isEmpty()) {

                } else {
                    postValue(dataResponse.data.results)

                }
            }
            is NetworkResult.Error -> {
                binding.progressView.visibility = View.GONE
                loadDataFromCache()
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

    private fun postValue(results: List<Result>) {
        if (results != null) {
            adapter = MoviePlayingAdapter(requireContext(), results)
            setupRecyclerView()
            adapter.setOnclicklistener(object : MoviePlayingAdapter.OnClickListener {
                override fun onClick(position: Int, model: Result) {
                    gotoDetailMovie(model.id)
//                    Toast.makeText(requireContext(),model.id.toString(),Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerView
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            viewModel.readMovies.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    adapter = MoviePlayingAdapter(
                        requireContext(),
                        database.first().responseMovie.results
                    )
                    setupRecyclerView()
                    binding.progressView.visibility = View.GONE

                    adapter.setOnclicklistener(object : MoviePlayingAdapter.OnClickListener {
                        override fun onClick(position: Int, model: Result) {
                            gotoDetailMovie(model.id)
                        }
                    })
                } else {
                    observeViewModel()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.readMovies.observe(viewLifecycleOwner) { database ->
                adapter = MoviePlayingAdapter(
                    requireContext(),
                    database.first().responseMovie.results
                )
                setupRecyclerView()
                binding.progressView.visibility = View.GONE

                adapter.setOnclicklistener(object : MoviePlayingAdapter.OnClickListener {
                    override fun onClick(position: Int, model: Result) {
                        gotoDetailMovie(model.id)
                    }
                })

            }
        }
    }

    private fun gotoDetailMovie(id: Int) {
        val action: NavDirections =
            ListMovieFragmentDirections.actionListMovieFragmentToDetailMovieFragment(id)
        Navigation.findNavController(binding.recyclerView).navigate(action)

    }

    private fun gotoSearchMovie() {
        val action: NavDirections =
            ListMovieFragmentDirections.actionListMovieFragmentToSearchMovieFragment()
        Navigation.findNavController(binding.toSearch).navigate(action)

    }
}