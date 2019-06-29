package com.pemwa.devbytesplayer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pemwa.devbytesplayer.database.getDatabaseInstance
import com.pemwa.devbytesplayer.repository.VideosRepository
import retrofit2.HttpException

/**
 * Using WorkManager to pre-fetch data when the app is in the background.
 * This ensures that users will get the freshest data every time they open the app.
 * WorkManager will make sure to schedule the work so it has the lowest impact on battery life possible.
 *
 * We use a CoroutineWorker, because we want to use coroutines to handle our asynchronous code and threading.
 */
class RefreshDataWork(
    appContext: Context,
    params: WorkerParameters) : CoroutineWorker(appContext, params) {

    /**
     * This is where our [RefreshDataWorker] does it work i.e syncing with the network.
     */
    override suspend fun doWork(): Result {
        val database = getDatabaseInstance(applicationContext)
        val repository = VideosRepository(database)

        // Here we return SUCCESS or RETRY
        return try {
            repository.refreshVideos()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }


}
