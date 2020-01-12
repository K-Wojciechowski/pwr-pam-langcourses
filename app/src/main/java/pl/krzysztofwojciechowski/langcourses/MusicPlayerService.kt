package pl.krzysztofwojciechowski.langcourses

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import java.io.File


enum class AudioState { PLAY, STOP }


class MusicPlayerService : Service() {
    var mediaPlayer = MediaPlayer()
    var files: List<File> = listOf()
    var nowPlaying: File? = null
    val updateHandler = Handler()
    var guiUpdateRunnable: Runnable? = null
    var state: AudioState = AudioState.STOP
    var stateChangeCallback: (() -> Unit)? = null
    private val musicBind = MusicBinder()

    // GUI updates and previous/pause handling
    private val updateRunnable = object : Runnable {
        override fun run() {
            guiUpdateRunnable?.run()
            if (mediaPlayer.isPlaying) {
                updateHandler.postDelayed(this, 50)
            } else {
                prevNext(1)
            }
        }
    }

    // Service management

    override fun onCreate() {
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return musicBind
    }

    override fun onUnbind(intent: Intent?): Boolean {
//        mediaPlayer.reset()
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            INTENT_STOP -> stopPlaying()
            INTENT_PLAYPAUSE -> playPause()
            INTENT_PREVIOUS -> prevNext(-1)
            INTENT_NEXT -> prevNext(1)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaying()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopPlaying()
    }

    // Interactions and music handling

    fun startPlaying(file: File) {
        mediaPlayer.stop()
        mediaPlayer.reset()
        nowPlaying = file
        try {
            mediaPlayer.setDataSource(file.absolutePath)
        } catch (ex: java.io.IOException) {
            android.util.Log.e("MLC_Player", "Failed to play: $file")
            prevNext(1)
            return
        }
        mediaPlayer.prepare()
        playPause()
    }

    fun stopPlaying() {
        stopUpdating()
        mediaPlayer.stop()
        nowPlaying = null
        setAudioState(AudioState.STOP)
    }

    private fun setAudioState(state: AudioState) {
        this.state = state
        stateChangeCallback?.invoke()
    }

    private fun playPause() {
        if (nowPlaying == null && files.isNotEmpty()) {
            startPlaying(files[0])
            return
        }
        setAudioState(AudioState.PLAY)
        mediaPlayer.start()
        startUpdating()
    }

    fun prevNext(shift: Int) {
        val position = files.indexOf(nowPlaying)
        var newPos = (position + shift)
        if (newPos >= files.size) {
            stopPlaying()
            return
        }
        if (position == -1 || newPos == -1) {
            newPos = if (shift == -1) files.size - 1 else 0
        }
        startPlaying(files[newPos])
    }

    // Timer helpers
    fun startUpdating() {
        updateHandler.postDelayed(updateRunnable, 0)
    }

    private fun stopUpdating() {
        updateHandler.removeCallbacks(updateRunnable)
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }
}
