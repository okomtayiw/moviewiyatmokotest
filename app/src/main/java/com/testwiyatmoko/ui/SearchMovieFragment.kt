package com.testwiyatmoko.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.testwiyatmoko.databinding.FragmentSearchMovieBinding
import com.testwiyatmoko.ui.adapter.MoviePlayingAdapter
import com.testwiyatmoko.until.NetworkListener
import com.testwiyatmoko.viewmodel.SearchMovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchMovieFragment : Fragment() {
    private var _binding: FragmentSearchMovieBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MoviePlayingAdapter
    private lateinit var networkListener: NetworkListener
    private val viewModel: SearchMovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchMovieBinding.inflate(inflater, container, false)

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    viewModel.networkStatus = status
                    viewModel.showNetworkStatus()
                }
        }



        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (query?.isNotEmpty() == true) {
                    observeViewModel()
                    viewModel.getSearchMovie(query.toString())
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

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
                dataResponse.data?.results?.let { postValue(it) }
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

    private fun postValue(results: List<Result>) {
        if (results != null) {
            val recyclerView: RecyclerView = binding.searchRecyclerview
            linearLayoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = linearLayoutManager
            adapter = MoviePlayingAdapter(requireContext(), results)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            adapter.setOnclicklistener(object : MoviePlayingAdapter.OnClickListener {
                override fun onClick(position: Int, model: Result) {
                    gotoDetailMovie(model.id)
                }

            })
        }
    }

    private fun gotoDetailMovie(id: Int){
        val action: NavDirections = SearchMovieFragmentDirections.actionSearchMovieFragmentToDetailMovieFragment(id)
        Navigation.findNavController(binding.searchRecyclerview).navigate(action)

    }
}