package com.pemwa.devbytesplayer.network

import com.pemwa.devbytesplayer.database.DatabaseVideo
import com.pemwa.devbytesplayer.domain.Video
import com.squareup.moshi.JsonClass

/**
* DataTransferObjects go in this file. These are responsible for parsing responses from the server
* or formatting objects to send to the server. You should convert these to domain objects before
* using them.
*/

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkVideoContainer(val videos: List<NetworkVideo>)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkVideo(
    val title: String,
    val description: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
    val closedCaptions: String?)

/**
 * An extension function that convert Network results to database objects
 */
fun NetworkVideoContainer.asDomainModel(): List<Video> {
    return videos.map {
        Video(
            title = it.title,
            description = it.description,
            url = it.url,
            updated = it.updated,
            thumbnail = it.thumbnail)
    }
}

/**
 * An extension function that converts from data transfer objects to database objects
 */
fun NetworkVideoContainer.asDatabaseModel() : Array<DatabaseVideo> {
    return videos.map {
        DatabaseVideo (
            title = it.title,
            description = it.description,
            url = it.url,
            updated = it.updated,
            thumbnail = it.thumbnail
        )
    }.toTypedArray()
}
