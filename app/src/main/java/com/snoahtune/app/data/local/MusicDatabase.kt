package com.snoahtune.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.snoahtune.app.data.local.dao.*
import com.snoahtune.app.data.local.entities.*

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
}
