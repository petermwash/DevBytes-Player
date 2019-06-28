package com.pemwa.devbytesplayer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * A Dao Interface for the video database
 */
@Dao
interface VideoDao {

    /**
     * A mapping function to get all the videos from the database
     * @return LiveData<List<DatabaseVideo>> returns a list of the DatabaseVideo objects as LiveData
     */
    @Query("SELECT * FROM video_database_table")
    fun getVideos() : LiveData<List<DatabaseVideo>>

    /**
     * A mapping function to update all the videos from the database
     * @param videos an array of DatabaseVideo objects fetched from the network
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: DatabaseVideo)

}

/**
 * Creating an instance of a RoomDatabase and declare what entity to use and the version
 */
@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase : RoomDatabase() {

    abstract val videoDao: VideoDao

    /**
     * A companion object to allow clients to access the methods
     * for creating or getting the database without instantiating the class
     */
    companion object {

        /**
         * The INSTANCE variable will keep a reference to the database once created
         * this helps in avoiding to repeatedly create connections to the db since its an expensive operation
         *
         * The "@Volatile" annotation ensures that the value of Instance is up to date and same to all execution threads
         * This is because the value of a Volatile variable will never be cashed and all read/write are done to/from the main memory
         * It means that changes made in one thread to INSTANCE are visible to all other threads immediately
         */
        @Volatile
        private var INSTANCE: VideosDatabase? = null
    }

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
        synchronized(this) {
            // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
            // Smart cast is only available to local variables.
            var instance = INSTANCE

            // If instance is `null` make a new database instance.
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideosDatabase::class.java,
                    "videos_database")
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
            }
            // Return instance; smart cast to be non-null.
            return instance
        }
    }
}
