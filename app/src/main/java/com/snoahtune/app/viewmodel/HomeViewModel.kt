package com.snoahtune.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snoahtune.app.data.local.entities.PlaylistEntity
import com.snoahtune.app.domain.model.Album
import com.snoahtune.app.domain.model.Song
import com.snoahtune.app.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder {
    DATE_ADDED_DESC, DATE_ADDED_ASC,
    NAME_ASC, NAME_DESC,
    DURATION_DESC, DURATION_ASC,
    ARTIST
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _allSongs   = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOrder   = MutableStateFlow(SortOrder.DATE_ADDED_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    private val _albums      = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    private val _isLoading   = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _playlists   = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    val playlists: StateFlow<List<PlaylistEntity>> = _playlists

    val filteredSongs: StateFlow<List<Song>> =
        combine(_allSongs, _searchQuery, _sortOrder) { songs, query, sort ->
            songs
                .filter { song ->
                    query.isBlank() ||
                        song.title.contains(query, true) ||
                        song.artist.contains(query, true) ||
                        song.album.contains(query, true)
                }
                .let { filtered ->
                    when (sort) {
                        SortOrder.NAME_ASC        -> filtered.sortedBy { it.title }
                        SortOrder.NAME_DESC       -> filtered.sortedByDescending { it.title }
                        SortOrder.DATE_ADDED_DESC -> filtered.sortedByDescending { it.dateAdded }
                        SortOrder.DATE_ADDED_ASC  -> filtered.sortedBy { it.dateAdded }
                        SortOrder.DURATION_DESC   -> filtered.sortedByDescending { it.duration }
                        SortOrder.DURATION_ASC    -> filtered.sortedBy { it.duration }
                        SortOrder.ARTIST          -> filtered.sortedBy { it.artist }
                    }
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<Song>> =
        repository.getFavoriteSongs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getAllSongs().collect { songs ->
                _allSongs.value = songs
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            repository.getAllPlaylists().collect { _playlists.value = it }
        }
    }

    fun refreshSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshSongs()
            _albums.value = kotlinx.coroutines.withContext(Dispatchers.IO) {
                repository.getAlbums()
            }
            _isLoading.value = false
        }
    }

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setSortOrder(s: SortOrder) { _sortOrder.value = s }

    // ── Playlist operations ─────────────────────────────────────
    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.createPlaylist(name.trim()) }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch { repository.deletePlaylist(playlist) }
    }

    fun addSongToPlaylist(playlistId: Int, songId: Long) {
        viewModelScope.launch { repository.addSongToPlaylist(playlistId, songId) }
    }

    fun renamePlaylist(id: Int, name: String) {
        viewModelScope.launch { repository.renamePlaylist(id, name) }
    }
}
