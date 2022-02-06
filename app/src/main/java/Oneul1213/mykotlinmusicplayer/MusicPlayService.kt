package Oneul1213.mykotlinmusicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class MusicPlayService : Service() {

    var musicUri: Uri? = null

    companion object {
        var musicPlayer: MediaPlayer? = null

        val ACTION_MUSIC_PLAY = "Oneul1213.mykotlinmusicplayer.MUSIC_PLAY"
        val ACTION_MUSIC_PAUSE = "Oneul1213.mykotlinmusicplayer.MUSIC_PAUSE"
        val ACTION_PREVIOUS_MUSIC = "Oneul1213.mykotlinmusicplayer.PREVIOUS_MUSIC"
        val ACTION_NEXT_MUSIC = "Oneul1213.mykotlinmusicplayer.NEXT_MUSIC"
    }

    inner class MusicPlayServiceBinder: Binder() {
        fun getService(): MusicPlayService {
            return this@MusicPlayService
        }
    }
    val binder = MusicPlayServiceBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.musicUri = intent?.getParcelableExtra("musicUri")
//        val action = intent?.action
//
//        when (action) {
//            ACTION_MUSIC_PLAY -> playMusic()
//            ACTION_MUSIC_PAUSE -> pauseMusic()
//            ACTION_PREVIOUS_MUSIC -> previousMusic()
//            ACTION_NEXT_MUSIC -> nextMusic()
//        }
        initMusicPlayer()
        playMusic()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initMusicPlayer() {
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(this, this.musicUri)
        } else {
            musicPlayer?.run {
                reset()
                // let은 scope 내부에서 immutable 보장..? -> Uri?가 Uri가 됨..?
                this@MusicPlayService.musicUri?.let {
                    musicPlayer?.setDataSource(this@MusicPlayService, it)
                }
                prepare()
            }
        }
    }

    fun playMusic() {
        musicPlayer?.start()
    }

    fun pauseMusic() {
        musicPlayer?.pause()
    }

    fun nextMusic() {

    }

    fun previousMusic() {

    }
}