package com.snoahtune.app.data.local

import android.content.Context
import android.provider.MediaStore
import com.snoahtune.app.domain.model.Album
import com.snoahtune.app.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getSongs(): List<Song> {
        val songs = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE
        )
        // Only real music files, at least 30 seconds long
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1 AND " +
                "${MediaStore.Audio.Media.DURATION} >= 30000"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idCol      = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol  = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durCol     = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizeCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataCol) ?: continue
                if (shouldExclude(path)) continue
                songs.add(
                    Song(
                        id        = cursor.getLong(idCol),
                        title     = cursor.getString(titleCol)   ?: "Unknown",
                        artist    = cursor.getString(artistCol)  ?: "Unknown",
                        album     = cursor.getString(albumCol)   ?: "Unknown",
                        albumId   = cursor.getLong(albumIdCol),
                        duration  = cursor.getLong(durCol),
                        path      = path,
                        dateAdded = cursor.getLong(dateCol),
                        size      = cursor.getLong(sizeCol)
                    )
                )
            }
        }
        return songs
    }

    // Exclude WhatsApp, voice recorders, ringtones, system sounds, etc.
    private fun shouldExclude(path: String): Boolean {
        val lower = path.lowercase()
        return lower.contains("/whatsapp/") ||
            lower.contains("/voice") ||
            lower.contains("recording") ||
            lower.contains("callrecord") ||
            lower.contains("/notifications/") ||
            lower.contains("/alarms/") ||
            lower.contains("/ringtones/") ||
            lower.contains("/system/")
    }

    fun getAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )
        context.contentResolver.query(uri, projection, null, null, MediaStore.Audio.Albums.ALBUM)
            ?.use { cursor ->
                val idCol    = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                val nameCol  = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                val artCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
                val cntCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
                while (cursor.moveToNext()) {
                    albums.add(
                        Album(
                            id        = cursor.getLong(idCol),
                            name      = cursor.getString(nameCol) ?: "Unknown",
                            artist    = cursor.getString(artCol)  ?: "Unknown",
                            songCount = cursor.getInt(cntCol)
                        )
                    )
                }
            }
        return albums
    }
}
