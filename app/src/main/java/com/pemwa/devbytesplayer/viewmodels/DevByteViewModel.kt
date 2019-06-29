package com.pemwa.devbytesplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pemwa.devbytesplayer.database.getDatabaseInstance
import com.pemwa.devbytesplayer.repository.VideosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * DevByteViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class DevByteViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * A database variable to hold the singleton instance of our db
     */
    private val database = getDatabaseInstance(application)

    /**
     * Creating an instance of the repository
     */
    private val videosRepository = VideosRepository(database)

    /**
     * Refreshing the videos using the repository.
     */
    init {
        viewModelScope.launch {
            videosRepository.refreshVideos()
        }
    }

    /**
     * Creating a videos playlist from the repository
     */
    val playlist = videosRepository.videos

//    /**
//     * A playlist of videos that can be shown on the screen. This is private to avoid exposing a
//     * way to set this value to observers.
//     */
//    private val _playlist = MutableLiveData<List<Video>>()
//
//    /**
//     * A playlist of videos that can be shown on the screen. Views should use this to get access
//     * to the data.
//     */
//    val playlist: LiveData<List<Video>>
//        get() = _playlist
//
//    /**
//     * init{} is called immediately when this ViewModel is created.
//     */
//    init {
//        refreshDataFromNetwork()
//    }
//
//    /**
//     * Refresh data from network and pass it via LiveData. Use a coroutine launch to get to
//     * background thread.
//     */
//    private fun refreshDataFromNetwork() = viewModelScope.launch {
//        try {
//            val playlist = Network.devbytes.getPlaylist().await()
//            _playlist.postValue(playlist.asDomainModel())
//        } catch (networkError: IOException) {
//            // Handle the error
//        }
//    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}