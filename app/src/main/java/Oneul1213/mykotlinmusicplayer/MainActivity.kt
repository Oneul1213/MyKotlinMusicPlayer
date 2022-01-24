package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    lateinit var musicListAdapter: MusicListAdapter
    val data = mutableListOf<MusicListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        data.apply {
            add(MusicListItem(null, "비가내리는날에는", "윤하"))
            add(MusicListItem(null, "기다리다", "윤하"))
        }

        initRecycler()
    }

    private fun initRecycler() {
        musicListAdapter = MusicListAdapter()
        musicListAdapter.data = data
        binding.recyclerViewMusicList.adapter = musicListAdapter
        binding.recyclerViewMusicList.layoutManager = LinearLayoutManager(this)
    }
}