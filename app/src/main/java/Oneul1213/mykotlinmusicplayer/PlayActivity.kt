package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityPlayBinding
import android.app.Service
import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
    var position: Int? = null

    var isPlayingMusic = true
    var jobMusicPlayService: Job? = null

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

    val musicResultBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // MainActivity에서 가져온 이전/다음 곡 정보로 Activity 설정
            getIntentExtras(intent)
            setViews()
            jobMusicPlayService?.cancel()
            jobMusicPlayService = CoroutineScope(Dispatchers.Default).launch {
                // 음악 정보로 서비스 실행
                val musicPlayServiceIntent = Intent(this@PlayActivity, MusicPlayService::class.java)

                musicPlayServiceIntent.action = MusicPlayService.ACTION_MUSIC_PLAY
                musicPlayServiceIntent.putExtra("musicUri", this@PlayActivity.musicUri)

                Log.d("PAonReceive", "Received")
                startService(musicPlayServiceIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // LocalBroadcastReceiver 등록
        val intentFilter = IntentFilter().apply { addAction(MusicPlayService.ACTION_MUSIC_RESULT) }
        LocalBroadcastManager.getInstance(this).registerReceiver(musicResultBroadcastReceiver, intentFilter)

        // intent에서 값 받아오기
        getIntentExtras(intent)

        // 음악 정보 설정
        setViews()

        // 코루틴에서 MusicPlayService 실행
        jobMusicPlayService = CoroutineScope(Dispatchers.Default).launch {
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
            // position과 action_next를 담은 broadcast 송신
            val nextMusicIntent = Intent(MusicPlayService.ACTION_NEXT_MUSIC)
            nextMusicIntent.putExtra("position", position)
            LocalBroadcastManager.getInstance(this).sendBroadcast(nextMusicIntent)
        }

        binding.imageViewPreviousPlay.setOnClickListener {
            // position과 action_previous를 담은 broadcast 송신
            val previousMusicIntent = Intent(MusicPlayService.ACTION_PREVIOUS_MUSIC)
            previousMusicIntent.putExtra("position", position)
            LocalBroadcastManager.getInstance(this).sendBroadcast(previousMusicIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicResultBroadcastReceiver)
        jobMusicPlayService?.cancel()
    }

    private fun getIntentExtras(intent: Intent?) {
        this.musicUri = intent?.getParcelableExtra("musicUri")
        this.albumUri = intent?.getParcelableExtra("albumUri")
        this.title = intent?.getStringExtra("title")
        this.artist = intent?.getStringExtra("artist")
        this.duration = intent?.getStringExtra("duration")
        this.position = intent?.getIntExtra("position", 0)
    }

    private fun setViews() {
        binding.imageViewAlbumArtPlay.setImageURI(this.albumUri)
        binding.textViewMusicTitlePlay.text = this.title
        binding.textViewArtistPlay.text = this.artist
        binding.textViewDurationPlay.text = "00:00 / ${this.duration}"
    }
}