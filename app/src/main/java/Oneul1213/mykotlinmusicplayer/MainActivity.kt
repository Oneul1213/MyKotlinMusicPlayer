package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    lateinit var musicListAdapter: MusicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getPermission()

        scanFiles()

        initRecycler()
    }

    // 권한 받아오기
    private fun getPermission() {
        val checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인됨", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 99)
        }
    }

    // 새로 추가된 파일 scan
    private fun scanFiles() {
        val path = arrayOf("/storage/")
        MediaScannerConnection.scanFile(
            applicationContext,
            path,
            null,
            MediaScannerConnection.OnScanCompletedListener {path, uri ->
                Log.i("onScanCompleted", "$path is scanned")
            }
        )
    }

    // RecyclerView 초기화
    private fun initRecycler() {
        musicListAdapter = MusicListAdapter()
        musicListAdapter.musicList.addAll(getMusicList())

        binding.recyclerViewMusicList.adapter = musicListAdapter
        binding.recyclerViewMusicList.layoutManager = LinearLayoutManager(this)
    }

    // 음악 리스트 생성
    private fun getMusicList(): List<MusicListItem> {
        val listUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val musicList = mutableListOf<MusicListItem>()

        contentResolver.query(
            listUrl,
            proj,
            null,
            null,
            sortOrder)?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1)
                val artist = cursor.getString(2)
                val albumId = cursor.getString(3)
                val duration = cursor.getLong(4)

                val music = MusicListItem(id, title, artist, albumId, duration)
                musicList.add(music)
            }
        }

        return musicList
    }
}