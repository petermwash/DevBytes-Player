package com.pemwa.devbytesplayer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.pemwa.devbytesplayer.database.VideosDatabase
import com.pemwa.devbytesplayer.database.asDomainModel
import com.pemwa.devbytesplayer.domain.Video
import com.pemwa.devbytesplayer.network.Network
import com.pemwa.devbytesplayer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching devbyts videos from the network and storing them on the disk.
 *
 * A Repository is just a regular class that has one (or more) methods that load data
 * without specifying the data source as part of the main API.
 * Because it's just a regular class, there's no need for an annotation to define a repository.
 *
 * The repository hides the complexity of managing the interactions between the database and the networking code.
 */
class VideosRepository(private val database: VideosDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val videos: LiveData<List<Video>> = Transformations.map(database.videoDao.getVideos()) {
        it.asDomainModel()
    }

    /**
     * Defining a "refreshVideos()" function to refresh the offline cache.
     * We make it a "suspend function" since it will bw called fro ta coroutine.
     *
     * This function uses the IO dispatcher to ensure that the insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, we observe [videos]
     */
    suspend fun refreshVideos() {

        // Run on the IO Dispatcher
        withContext(Dispatchers.IO) {

            // A network call to getPlaylist(), and we use the await() function to tell the coroutine
            // to suspend until the data is available.
            val playList = Network.devbytes.getPlaylist().await()

            // Then we call insertAll() to insert the playlist into the database
            // The asterisk * is the spread operator. It allows us to pass in an array to a function that expects varargs.
            database.videoDao.insertAll(*playList.asDatabaseModel())

        }
    }
}
