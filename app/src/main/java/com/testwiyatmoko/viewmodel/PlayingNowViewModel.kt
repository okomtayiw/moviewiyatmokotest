package com.testwiyatmoko.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.*
import com.testwiyatmoko.data.network.NetworkResult
import com.testwiyatmoko.data.ResponseMovie
import com.testwiyatmoko.data.database.entity.MoviesEntity
import com.testwiyatmoko.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PlayingNowViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    var networkStatus = false
    var backOnline = false

    val readMovies: LiveData<List<MoviesEntity>> = repository.local.readMovies().asLiveData()


    private fun insertMovies(moviesEntity: MoviesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertMovies(moviesEntity)
        }


    // retrofit
    var responseMovie: MutableLiveData<NetworkResult<ResponseMovie>> = MutableLiveData()



    fun getPlayingMovie() = viewModelScope.launch {
        responseMovie.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getMoviePlaying(1, "6bc90b57c5647cd87ee44270e4b52998")
                responseMovie.value = handleResponsePlayingMovie(response)
                val movies = responseMovie.value!!.data
                if(movies != null) {
                    offlineCacheMovies(movies)
                }
            } catch (e: Exception) {
                responseMovie.value = NetworkResult.Error("Not Found ")
            }


        } else {
            responseMovie.value = NetworkResult.Error("No Internet Connection")
        }

    }


    private fun offlineCacheMovies(responseMovie: ResponseMovie) {
        val moviesEntity= MoviesEntity(responseMovie)
        insertMovies(moviesEntity)
    }

    private fun handleResponsePlayingMovie(response: Response<ResponseMovie>): NetworkResult<ResponseMovie>? {

        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 401 -> {
                return NetworkResult.Error("Access denied")
            }
            response.isSuccessful -> {
                val responseMovie = response.body()
                return NetworkResult.Success(responseMovie!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }

    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork ?: return false
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}