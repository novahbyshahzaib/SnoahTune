package com.snoahtune.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snoahtune.app.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String,
    val dateAdded: Long,
    val size: Long
)

fun SongEntity.toDomain() = Song(id, title, artist, album, albumId, duration, path, dateAdded, size)
fun Song.toEntity() = SongEntity(id, title, artist, album, albumId, duration, path, dateAdded, size)
