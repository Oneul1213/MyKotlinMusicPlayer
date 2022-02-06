package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityPlayBinding
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlayActivity : AppCompatActivity() {

    val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }

    var musicUri: Uri? = null
    var albumUri: Uri? = null
    var title: String? = null
    var artist: String? = null
    var duration: String? = null

    var isPlayingMusic = true

    var musicPlayService: MusicPlayService? = null
    var isService = false
    val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayService.MusicPlayServiceBinder
            musicPlayService = binder.getService()
            isService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // intent에서 값 받아오기
        this.musicUri = intent.getParcelableExtra("musicUri")
        this.albumUri = intent.getParcelableExtra("albumUri")
        this.title = intent.getStringExtra("title")
        this.artist = intent.getStringExtra("artist")
        this.duration = intent.getStringExtra("duration")

        // 음악 정보 설정
        binding.imageViewAlbumArtPlay.setImageURI(this.albumUri)
        binding.textViewMusicTitlePlay.text = this.title
        binding.textViewArtistPlay.text = this.artist
        binding.textViewDurationPlay.text = "00:00 / ${this.duration}"

        // 코루틴에서 MusicPlayService 실행
        CoroutineScope(Dispatchers.Default).launch {
            val musicPlayServiceIntent = Intent(this@PlayActivity, MusicPlayService::class.java)

            musicPlayServiceIntent.action = MusicPlayService.ACTION_MUSIC_PLAY
            musicPlayServiceIntent.putExtra("musicUri", this@PlayActivity.musicUri)

            startService(musicPlayServiceIntent)
        }

        // 서비스 바인드
        val bindServiceIntent = Intent(this, MusicPlayService::class.java)
        bindService(bindServiceIntent, connection, Context.BIND_AUTO_CREATE)

        // clickListener 설정
        binding.imageViewTogglePlay.setOnClickListener {
            when (isPlayingMusic) {
                // 재생 중에 일시정지를 위해 클릭
                true -> {
                    isPlayingMusic = false
                    binding.imageViewTogglePlay.setImageResource(R.drawable.icon_play)

                    if (isService) {
                        musicPlayService?.pauseMusic()
                    }
                }

                // 일시정지 중에 재생을 위해 클릭
                false -> {
                    isPlayingMusic = true
                    binding.imageViewTogglePlay.setImageResource(R.drawable.icon_pause)

                    if (isService) {
                        musicPlayService?.playMusic()
                    }
                }
            }
        }

        binding.imageViewNextPlay.setOnClickListener {

        }

        binding.imageViewPreviousPlay.setOnClickListener {

        }
    }
}