package com.pemwa.devbytesplayer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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
