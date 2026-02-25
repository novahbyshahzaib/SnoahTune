package com.snoahtune.app.viewmodel

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.*
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.snoahtune.app.domain.model.Song
import com.snoahtune.app.domain.repository.MusicRepository
import com.snoahtune.app.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MusicRepository
) : ViewModel() {

    private var player: Player? = null

    private val _currentSong   = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying     = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress      = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _currentPos    = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPos

    private val _shuffleOn     = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleOn

    private val _repeatMode    = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _playbackSpeed = MutableStateFlow(1f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    private val _queue         = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue

    private val _isFavorite    = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // Slowed + Reverb: when ON, pitch matches speed (lower pitch = dreamy slowed sound)
    private val _slowedReverbOn = MutableStateFlow(false)
    val slowedReverbOn: StateFlow<Boolean> = _slowedReverbOn

    fun connectToService() {
        val token = SessionToken(context, ComponentName(context, MusicService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener({
            player = future.get()
            attachListeners()
            startProgressPoller()
        }, MoreExecutors.directExecutor())
    }

    private fun attachListeners() {
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) { _isPlaying.value = isPlaying }
            override fun onRepeatModeChanged(mode: Int)         { _repeatMode.value = mode }
            override fun onShuffleModeEnabledChanged(on: Boolean) { _shuffleOn.value = on }
            override fun onMediaItemTransition(item: MediaItem?, reason: Int) { syncCurrentSong() }
        })
    }

    private fun startProgressPoller() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                player?.let { p ->
                    _currentPos.value = p.currentPosition
                    val dur = p.duration.takeIf { it > 0 } ?: 1L
                    _progress.value = (p.currentPosition.toFloat() / dur).coerceIn(0f, 1f)
                }
            }
        }
    }

    private fun syncCurrentSong() {
        val idx = player?.currentMediaItemIndex ?: return
        if (idx < _queue.value.size) {
            val song = _queue.value[idx]
            _currentSong.value = song
            viewModelScope.launch {
                repository.isFavorite(song.id).collect { _isFavorite.value = it }
            }
        }
    }

    fun playSong(song: Song, queue: List<Song>) {
        _queue.value = queue
        val startIdx = queue.indexOf(song).coerceAtLeast(0)
        val items = queue.map { s ->
            MediaItem.Builder()
                .setMediaId(s.id.toString())
                .setUri(s.path)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.artist)
                        .setAlbumTitle(s.album)
                        .setArtworkUri(
                            Uri.parse("content://media/external/audio/albumart/${s.albumId}")
                        )
                        .build()
                )
                .build()
        }
        player?.apply {
            setMediaItems(items, startIdx, C.TIME_UNSET)
            prepare()
            play()
        }
        _currentSong.value = song
        viewModelScope.launch {
            repository.isFavorite(song.id).collect { _isFavorite.value = it }
        }
    }

    fun playPause()    { player?.let { if (it.isPlaying) it.pause() else it.play() } }
    fun skipNext()     { player?.seekToNextMediaItem() }
    fun skipPrevious() { player?.seekToPreviousMediaItem() }

    fun seekTo(fraction: Float) {
        player?.let { it.seekTo((it.duration * fraction).toLong()) }
    }

    fun toggleShuffle() {
        player?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }

    fun toggleRepeat() {
        player?.let {
            it.repeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else                   -> Player.REPEAT_MODE_OFF
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        applyPlaybackParameters()
    }

    /**
     * Toggle Slowed + Reverb mode.
     * When ON:  pitch follows speed — slowing down also lowers pitch (dreamy slowed sound).
     * When OFF: pitch is always 1.0 regardless of speed (normal time-stretch, voice unchanged).
     */
    fun toggleSlowedReverb() {
        _slowedReverbOn.value = !_slowedReverbOn.value
        applyPlaybackParameters()
    }

    private fun applyPlaybackParameters() {
        val speed = _playbackSpeed.value
        // When slowed+reverb is ON, pitch = speed value so lowering speed lowers pitch together
        val pitch = if (_slowedReverbOn.value) speed else 1f
        player?.playbackParameters = PlaybackParameters(speed, pitch)
    }

    fun toggleFavorite() {
        _currentSong.value?.let { song ->
            viewModelScope.launch { repository.toggleFavorite(song.id) }
        }
    }

    companion object {
        fun msToString(ms: Long): String {
            val s = ms / 1000
            return "%d:%02d".format(s / 60, s % 60)
        }
    }
}
