package com.snoahtune.app.domain.repository

import com.snoahtune.app.data.local.entities.PlaylistEntity
import com.snoahtune.app.data.local.entities.PlaylistWithSongs
import com.snoahtune.app.domain.model.Album
import com.snoahtune.app.domain.model.Artist
import com.snoahtune.app.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getFavoriteSongs(): Flow<List<Song>>
    fun isFavorite(songId: Long): Flow<Boolean>
    suspend fun toggleFavorite(songId: Long)
    suspend fun refreshSongs()
    fun getAlbums(): List<Album>
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun createPlaylist(name: String)
    suspend fun deletePlaylist(playlist: PlaylistEntity)
    suspend fun addSongToPlaylist(playlistId: Int, songId: Long)
    fun getPlaylistWithSongs(playlistId: Int): Flow<PlaylistWithSongs?>
    suspend fun renamePlaylist(id: Int, name: String)
}
