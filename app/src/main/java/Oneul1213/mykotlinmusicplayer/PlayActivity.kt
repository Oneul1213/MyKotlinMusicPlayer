package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityPlayBinding
import android.app.Service
import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayActivity : AppCompatActivity() {

    val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }

    var musicUri: Uri? = null
    var albumUri: Uri? = null
    var title: String? = null
    var artist: String? = null
    var duration: Long? = null
    var position: Int? = null

    var isPlayingMusic = true
    var jobMusicPlayService: Job? = null
    var jobUpdateSeekBar: Job? = null

    var musicPlayService: MusicPlayService? = null
    var isService = false
    val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayService.MusicPlayServiceBinder
            musicPlayService = binder.getService()
            isService = true

            initSeekBar()
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

                // 서비스 바인드
                val bindServiceIntent = Intent(this@PlayActivity, MusicPlayService::class.java)
                unbindService(connection)
                bindService(bindServiceIntent, connection, Context.BIND_AUTO_CREATE)
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
        if (isService) {
            unbindService(connection)
            isService = false
        }
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
            // 다음 곡 버튼을 누르면 다음 곡이 자동 재생
            isPlayingMusic = true
            binding.imageViewTogglePlay.setImageResource(R.drawable.icon_pause)

            // position과 action_next를 담은 broadcast 송신
            val nextMusicIntent = Intent(MusicPlayService.ACTION_NEXT_MUSIC)
            nextMusicIntent.putExtra("position", position)
            LocalBroadcastManager.getInstance(this).sendBroadcast(nextMusicIntent)
        }

        binding.imageViewPreviousPlay.setOnClickListener {
            // 이전 곡 버튼을 누르면 이전 곡이 자동 재생
            isPlayingMusic = true
            binding.imageViewTogglePlay.setImageResource(R.drawable.icon_pause)

            // position과 action_previous를 담은 broadcast 송신
            val previousMusicIntent = Intent(MusicPlayService.ACTION_PREVIOUS_MUSIC)
            previousMusicIntent.putExtra("position", position)
            LocalBroadcastManager.getInstance(this).sendBroadcast(previousMusicIntent)
        }

        // SeekBar 초기화
        val seekBar = binding.seekBarPlay
//        seekBar.max = musicPlayService?.getDuration() ?: 100
//        seekBar.progress = 0

        // SeekBar 1초마다 업데이트
        jobUpdateSeekBar = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                Thread.sleep(1000)
                if (isService && isPlayingMusic) {
                    val currentPosition = musicPlayService?.getCurrentProgress()
//                    val min = current_position?.let { it / (1000 * 60) }
//                    val sec = current_position?.let { it % (1000 * 60) }
                    val currentPositionString = SimpleDateFormat("mm:ss").format(currentPosition)
                    currentPosition?.let {
                        seekBar.incrementProgressBy(1000)
                    }

                    withContext(Dispatchers.Main) {
                        val duration = SimpleDateFormat("mm:ss").format(duration)
                        binding.textViewDurationPlay.text = "$currentPositionString / $duration"
                    }
                }
            }
        }

        // SeekBar 드래그 설정
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isService && fromUser) {
                    musicPlayService?.setProgress(progress)
                    val currentPositionString = SimpleDateFormat("mm:ss").format(progress)
                    val duration = SimpleDateFormat("mm:ss").format(duration)
                    binding.textViewDurationPlay.text = "$currentPositionString / $duration"
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
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
        this.duration = intent?.getLongExtra("duration", 0)
        this.position = intent?.getIntExtra("position", 0)
    }

    private fun setViews() {
        binding.imageViewAlbumArtPlay.setImageURI(this.albumUri)
        binding.textViewMusicTitlePlay.text = this.title
        binding.textViewArtistPlay.text = this.artist
        val duration = SimpleDateFormat("mm:ss").format(this.duration)
        binding.textViewDurationPlay.text = "00:00 / $duration"
    }

    private fun initSeekBar() {
        this.duration?.let {
            binding.seekBarPlay.max = it.toInt()
        }
        binding.seekBarPlay.progress = 0
    }
}