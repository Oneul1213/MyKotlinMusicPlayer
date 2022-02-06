package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.ActivityMainBinding
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import java.text.FieldPosition
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    val musicList = mutableListOf<MusicListItem>()
    lateinit var musicListAdapter: MusicListAdapter

    val musicBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // RecyclerView에서 현재 위치를 얻어옴
            val position: Int = intent?.getIntExtra("position", 0) ?: 0

            when(intent?.action) {
                // 다음 곡을 누른 상태라면
                MusicPlayService.ACTION_NEXT_MUSIC -> {
                    val nextPosition: Int
                    if (position == musicList.size-1) {
                        nextPosition = 0
                    } else {
                        nextPosition = position + 1
                    }
                    // musicList에서 다음 곡 가져오기
                    val nextMusic: MusicListItem = musicList[nextPosition]
                    Log.d("MAonReceive", "nextPosition=$nextPosition")
                    val nextMusicIntent = Intent(MusicPlayService.ACTION_MUSIC_RESULT).apply {
                        putExtra("musicUri", nextMusic.getMusicUri())
                        putExtra("albumUri", nextMusic.getAlbumUri())
                        putExtra("title", nextMusic.title)
                        putExtra("artist", nextMusic.artist)
                        val duration = SimpleDateFormat("mm:ss").format(nextMusic.duration)
                        putExtra("duration", duration)
                        putExtra("position", nextPosition)
                    }
                    // playActivity로 보냄
                    LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(nextMusicIntent)
                }

                // 이전 곡을 누른 상태라면
                MusicPlayService.ACTION_PREVIOUS_MUSIC -> {
                    val previousPosition: Int
                    if (position == 0) {
                        previousPosition = musicList.size - 1
                    } else {
                        previousPosition = position - 1
                    }
                    // musicList에서 이전 곡 가져오기
                    val previousMusic: MusicListItem = musicList[previousPosition]

                    val previousMusicIntent = Intent(MusicPlayService.ACTION_MUSIC_RESULT).apply {
                        putExtra("musicUri", previousMusic.getMusicUri())
                        putExtra("albumUri", previousMusic.getAlbumUri())
                        putExtra("title", previousMusic.title)
                        putExtra("artist", previousMusic.artist)
                        val duration = SimpleDateFormat("mm:ss").format(previousMusic.duration)
                        putExtra("duration", duration)
                        putExtra("position", previousPosition)
                    }
                    // playActivity로 보냄
                    LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(previousMusicIntent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // LocalBroadcastReceiver 등록
        val intentFilter = IntentFilter().apply {
            addAction(MusicPlayService.ACTION_NEXT_MUSIC)
            addAction(MusicPlayService.ACTION_PREVIOUS_MUSIC)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(musicBroadcastReceiver, intentFilter)

        getPermission()

        scanFiles()

        initRecycler()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicBroadcastReceiver)
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
            MediaScannerConnection.OnScanCompletedListener { path, uri ->
                Log.i("onScanCompleted", "$path is scanned")
            }
        )
    }

    // RecyclerView 초기화
    private fun initRecycler() {
        musicListAdapter = MusicListAdapter()
        setMusicList()
        musicListAdapter.musicList.addAll(musicList)

        binding.recyclerViewMusicList.adapter = musicListAdapter
        binding.recyclerViewMusicList.layoutManager = LinearLayoutManager(this)
    }

    // 음악 리스트 생성
    private fun setMusicList() {
        val listUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

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
    }
}