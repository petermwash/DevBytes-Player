package com.pemwa.devbytesplayer

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pemwa.devbytesplayer.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
class DevByteApplication : Application() {

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * We use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        delayedInit()
    }

    /**
     * A coroutine scope to use for the application
     */
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /**
     * An initialization function that does not block the main thread
     */
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    /**
     * Making a PeriodWorkRequest for the RefreshDataWorker.
     * It runs only once every day.
     */
    private fun setupRecurringWork() {
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            1, TimeUnit.DAYS
        ).build()

        // Schedule the work as unique
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
    }
}
