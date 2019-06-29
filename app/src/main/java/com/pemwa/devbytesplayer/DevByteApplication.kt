package com.pemwa.devbytesplayer

import android.app.Application
import android.os.Build
import androidx.work.*
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
     * An initialization function that does not block the main thread.
     * It runs the [setupRecurringWork] in the coroutine.
     */
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    /**
     * Making a PeriodWorkRequest for the RefreshDataWorker.
     * It runs only once every day an with the defined constraints.
     */
    private fun setupRecurringWork() {

        // Using a Builder to define constraints.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        // Set up and schedule a PeriodicWorkRequest
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // Schedule the work as unique
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
    }
}
