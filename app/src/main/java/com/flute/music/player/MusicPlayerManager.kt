package com.flute.music.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.flute.music.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerManager(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(appContext).build()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    val currentSong: Song get() = _playerState.value.currentSong
    val isPlaying: Boolean get() = _playerState.value.isPlaying

    private var positionUpdateJob: Job? = null

    // Shuffle support
    private var shuffledIndices: List<Int> = emptyList()
    private var originalQueue: List<Song> = emptyList()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        _playerState.update { state ->
                            state.copy(duration = exoPlayer.duration.coerceAtLeast(0L))
                        }
                    }
                    Player.STATE_ENDED -> {
                        handleSongCompletion()
                    }
                    else -> { /* STATE_IDLE, STATE_BUFFERING */ }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                _playerState.update { it.copy(isPlaying = playing) }
                if (playing) {
                    startPositionUpdates()
                } else {
                    stopPositionUpdates()
                }
            }
        })
    }

    fun playSong(song: Song) {
        playAll(listOf(song), 0)
    }

    fun playAll(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        val safeIndex = startIndex.coerceIn(0, songs.lastIndex)

        originalQueue = songs
        _playerState.update { state ->
            state.copy(
                queue = songs,
                currentIndex = safeIndex,
                currentSong = songs[safeIndex],
                currentPosition = 0L,
                duration = 0L
            )
        }

        if (_playerState.value.shuffleEnabled) {
            rebuildShuffledIndices(safeIndex)
        }

        loadAndPlay(songs[safeIndex])
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun resume() {
        exoPlayer.play()
    }

    fun next() {
        val state = _playerState.value
        if (state.queue.isEmpty()) return

        val nextIndex = getNextIndex(state)
        if (nextIndex != null) {
            val song = state.queue[nextIndex]
            _playerState.update {
                it.copy(
                    currentIndex = nextIndex,
                    currentSong = song,
                    currentPosition = 0L,
                    duration = 0L
                )
            }
            loadAndPlay(song)
        } else {
            // End of queue, no repeat
            exoPlayer.pause()
            exoPlayer.seekTo(0)
            _playerState.update { it.copy(isPlaying = false, currentPosition = 0L) }
        }
    }

    fun previous() {
        val state = _playerState.value
        if (state.queue.isEmpty()) return

        // If more than 3 seconds in, restart current song
        if (exoPlayer.currentPosition > 3000) {
            exoPlayer.seekTo(0)
            _playerState.update { it.copy(currentPosition = 0L) }
            return
        }

        val prevIndex = getPreviousIndex(state)
        val song = state.queue[prevIndex]
        _playerState.update {
            it.copy(
                currentIndex = prevIndex,
                currentSong = song,
                currentPosition = 0L,
                duration = 0L
            )
        }
        loadAndPlay(song)
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _playerState.update { it.copy(currentPosition = position) }
    }

    fun toggleShuffle() {
        val newShuffle = !_playerState.value.shuffleEnabled
        _playerState.update { it.copy(shuffleEnabled = newShuffle) }
        if (newShuffle) {
            rebuildShuffledIndices(_playerState.value.currentIndex)
        } else {
            shuffledIndices = emptyList()
        }
    }

    fun cycleRepeatMode() {
        val newMode = when (_playerState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playerState.update { it.copy(repeatMode = newMode) }
    }

    fun addToQueue(song: Song) {
        _playerState.update { state ->
            val newQueue = state.queue + song
            state.copy(queue = newQueue)
        }
        originalQueue = _playerState.value.queue
        if (_playerState.value.shuffleEnabled) {
            shuffledIndices = shuffledIndices + (_playerState.value.queue.lastIndex)
        }
    }

    fun removeFromQueue(index: Int) {
        val state = _playerState.value
        if (index < 0 || index >= state.queue.size) return

        val newQueue = state.queue.toMutableList().apply { removeAt(index) }
        val newIndex = when {
            newQueue.isEmpty() -> -1
            index < state.currentIndex -> state.currentIndex - 1
            index == state.currentIndex -> {
                // Currently playing song removed, play next or stop
                if (newQueue.isNotEmpty()) {
                    val nextIdx = index.coerceAtMost(newQueue.lastIndex)
                    loadAndPlay(newQueue[nextIdx])
                    nextIdx
                } else {
                    exoPlayer.stop()
                    -1
                }
            }
            else -> state.currentIndex
        }

        _playerState.update {
            it.copy(
                queue = newQueue,
                currentIndex = newIndex,
                currentSong = if (newIndex >= 0 && newIndex < newQueue.size) newQueue[newIndex] else Song.EMPTY
            )
        }
        originalQueue = newQueue

        if (_playerState.value.shuffleEnabled) {
            rebuildShuffledIndices(newIndex)
        }
    }

    fun clearQueue() {
        exoPlayer.stop()
        _playerState.update {
            PlayerState()
        }
        originalQueue = emptyList()
        shuffledIndices = emptyList()
    }

    fun getExoPlayer(): ExoPlayer = exoPlayer

    fun release() {
        stopPositionUpdates()
        exoPlayer.release()
    }

    // --- Private helpers ---

    private fun loadAndPlay(song: Song) {
        val uri = if (song.path.startsWith("http://") || song.path.startsWith("https://")) {
            Uri.parse(song.path)
        } else {
            Uri.parse(song.path)
        }

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .apply {
                if (song.albumArtUri.isNotEmpty()) {
                    setArtworkUri(Uri.parse(song.albumArtUri))
                }
            }
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(mediaMetadata)
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun handleSongCompletion() {
        val state = _playerState.value
        when (state.repeatMode) {
            RepeatMode.ONE -> {
                exoPlayer.seekTo(0)
                exoPlayer.play()
            }
            RepeatMode.ALL -> {
                next()
            }
            RepeatMode.OFF -> {
                val nextIndex = getNextIndexRaw(state)
                if (nextIndex != null) {
                    next()
                } else {
                    // Last song in queue, stop
                    _playerState.update {
                        it.copy(isPlaying = false, currentPosition = 0L)
                    }
                }
            }
        }
    }

    private fun getNextIndex(state: PlayerState): Int? {
        if (state.queue.isEmpty()) return null

        return if (state.shuffleEnabled && shuffledIndices.isNotEmpty()) {
            val currentShufflePos = shuffledIndices.indexOf(state.currentIndex)
            val nextShufflePos = currentShufflePos + 1
            if (nextShufflePos < shuffledIndices.size) {
                shuffledIndices[nextShufflePos]
            } else if (state.repeatMode == RepeatMode.ALL) {
                rebuildShuffledIndices(-1)
                shuffledIndices.firstOrNull()
            } else {
                null
            }
        } else {
            val nextIndex = state.currentIndex + 1
            if (nextIndex < state.queue.size) {
                nextIndex
            } else if (state.repeatMode == RepeatMode.ALL) {
                0
            } else {
                null
            }
        }
    }

    private fun getNextIndexRaw(state: PlayerState): Int? {
        if (state.queue.isEmpty()) return null
        return if (state.shuffleEnabled && shuffledIndices.isNotEmpty()) {
            val currentShufflePos = shuffledIndices.indexOf(state.currentIndex)
            val nextShufflePos = currentShufflePos + 1
            if (nextShufflePos < shuffledIndices.size) shuffledIndices[nextShufflePos] else null
        } else {
            val nextIndex = state.currentIndex + 1
            if (nextIndex < state.queue.size) nextIndex else null
        }
    }

    private fun getPreviousIndex(state: PlayerState): Int {
        return if (state.shuffleEnabled && shuffledIndices.isNotEmpty()) {
            val currentShufflePos = shuffledIndices.indexOf(state.currentIndex)
            val prevShufflePos = currentShufflePos - 1
            if (prevShufflePos >= 0) {
                shuffledIndices[prevShufflePos]
            } else {
                shuffledIndices.last()
            }
        } else {
            val prevIndex = state.currentIndex - 1
            if (prevIndex >= 0) prevIndex else state.queue.lastIndex
        }
    }

    private fun rebuildShuffledIndices(keepFirst: Int) {
        val queue = _playerState.value.queue
        if (queue.isEmpty()) {
            shuffledIndices = emptyList()
            return
        }
        val indices = queue.indices.toMutableList()
        if (keepFirst in indices) {
            indices.remove(keepFirst)
            indices.shuffle()
            shuffledIndices = listOf(keepFirst) + indices
        } else {
            indices.shuffle()
            shuffledIndices = indices
        }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = scope.launch {
            while (isActive) {
                val position = exoPlayer.currentPosition.coerceAtLeast(0L)
                val duration = exoPlayer.duration.coerceAtLeast(0L)
                _playerState.update {
                    it.copy(currentPosition = position, duration = duration)
                }
                delay(200L)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }
}
