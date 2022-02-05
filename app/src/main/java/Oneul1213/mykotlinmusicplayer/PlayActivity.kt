package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityMainBinding
import Oneul1213.mykotlinmusicplayer.databinding.ActivityPlayBinding
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlayActivity : AppCompatActivity() {

    val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }

    var albumUri: Uri? = null
    var title: String? = null
    var artist: String? = null
    var duration: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // intent에서 값 받아오기
        this.albumUri = intent.getParcelableExtra("albumUri")
        this.title = intent.getStringExtra("title")
        this.artist = intent.getStringExtra("artist")
        this.duration = intent.getStringExtra("duration")

        // 음악 정보 설정
        binding.imageViewAlbumArtPlay.setImageURI(this.albumUri)
        binding.textViewMusicTitlePlay.text = this.title
        binding.textViewArtistPlay.text = this.artist
        binding.textViewDurationPlay.text = "00:00 / ${this.duration}"
    }
}