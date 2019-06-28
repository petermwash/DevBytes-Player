package com.pemwa.devbytesplayer.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pemwa.devbytesplayer.domain.Video

/**
 * A data class defining the [DatabaseVideo] object/entity
 */
@Entity(tableName = "video_database_table")
data class DatabaseVideo constructor(

    @PrimaryKey
    val url: String,
    val updated: String,
    val title: String,
    val description: String,
    val thumbnail: String)

/**
 * An extension function which converts from database objects to domain objects
 */
fun List<DatabaseVideo>.asDomainModel() : List<Video> {
    return map {
        Video(
            url = it.url,
            title = it.title,
            description = it.description,
            updated = it.updated,
            thumbnail = it.thumbnail
        )
    }
}
