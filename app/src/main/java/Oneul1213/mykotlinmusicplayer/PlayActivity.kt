package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityMainBinding
import Oneul1213.mykotlinmusicplayer.databinding.ActivityPlayBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlayActivity : AppCompatActivity() {

    val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}