package com.pemwa.devbytesplayer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Creating an instance of a RoomDatabase and declare what entity to use and the version
 */
@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase : RoomDatabase() {

    abstract val videoDao: VideoDao
}

/**
 * The INSTANCE variable will keep a reference to the database once created
 * this helps in avoiding to repeatedly create connections to the db since its an expensive operation
 *
 * The "@Volatile" annotation ensures that the value of Instance is up to date and same to all execution threads
 * This is because the value of a Volatile variable will never be cashed and all read/write are done to/from the main memory
 * It means that changes made in one thread to INSTANCE are visible to all other threads immediately
 */
@Volatile
private lateinit var INSTANCE: VideosDatabase

/**
 * Helper function to get the database instance.
 *
 * If a database has already been retrieved, the previous database will be returned.
 * Otherwise, create a new database.
 *
 * This function is threadsafe, and callers should cache the result for multiple database
 * calls to avoid overhead.
 *
 * @param context The application context Singleton, used to get access to the filesystem.
 * @return INSTANCE The database instance.
 */
fun getDatabaseInstance(context: Context): VideosDatabase {
    /**
     *  Multiple threads can ask for the database at the same time, ensure we only initialize
     * it once by using synchronized. Only one thread may enter a synchronized block at a
     * time.
     */
    synchronized(VideosDatabase::class.java) {

        // If instance is `null` make a new database instance.
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                VideosDatabase::class.java,
                "videos_database")
                // Wipes and rebuilds instead of migrating if no Migration object.
                .fallbackToDestructiveMigration()
                .build()
        }
        // Return instance
        return INSTANCE
    }
}
