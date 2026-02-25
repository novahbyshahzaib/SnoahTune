package com.snoahtune.app.data.repository

import com.snoahtune.app.data.local.MediaStoreDataSource
import com.snoahtune.app.data.local.dao.FavoriteDao
import com.snoahtune.app.data.local.dao.PlaylistDao
import com.snoahtune.app.data.local.dao.SongDao
import com.snoahtune.app.data.local.entities.*
import com.snoahtune.app.domain.model.Album
import com.snoahtune.app.domain.model.Song
import com.snoahtune.app.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val mediaStore: MediaStoreDataSource,
    private val songDao: SongDao,
    private val favoriteDao: FavoriteDao,
    private val playlistDao: PlaylistDao
) : MusicRepository {

    override fun getAllSongs(): Flow<List<Song>> =
        songDao.getAllSongs().map { it.map { e -> e.toDomain() } }

    override fun getFavoriteSongs(): Flow<List<Song>> =
        combine(songDao.getAllSongs(), favoriteDao.getAllFavorites()) { songs, favs ->
            val ids = favs.map { it.songId }.toSet()
            songs.filter { it.id in ids }.map { it.toDomain() }
        }

    override fun isFavorite(songId: Long): Flow<Boolean> = favoriteDao.isFavorite(songId)

    override suspend fun toggleFavorite(songId: Long) {
        val isFav = favoriteDao.isFavorite(songId).first()
        if (isFav) favoriteDao.removeFavorite(FavoriteEntity(songId))
        else favoriteDao.addFavorite(FavoriteEntity(songId))
    }

    override suspend fun refreshSongs() {
        val songs = kotlinx.coroutines.withContext(Dispatchers.IO) { mediaStore.getSongs() }
        songDao.deleteAll()
        songDao.insertAll(songs.map { it.toEntity() })
    }

    override fun getAlbums(): List<Album> = mediaStore.getAlbums()

    override fun getAllPlaylists() = playlistDao.getAllPlaylists()

    override suspend fun createPlaylist(name: String) {
        playlistDao.createPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }

    override suspend fun addSongToPlaylist(playlistId: Int, songId: Long) {
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId))
    }

    override fun getPlaylistWithSongs(playlistId: Int) =
        playlistDao.getPlaylistWithSongs(playlistId)

    override suspend fun renamePlaylist(id: Int, name: String) {
        playlistDao.renamePlaylist(id, name)
    }
}
