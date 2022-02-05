package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.MusicListItemBinding
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MusicListAdapter() : RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
    var musicList = mutableListOf<MusicListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MusicListItemBinding.inflate(LayoutInflater.from(parent.context),
                                                                parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = musicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setMusic(musicList[position])
    }

    inner class ViewHolder(val binding: MusicListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var musicUri: Uri? = null
        var albumUri: Uri? = null
        var title: String? = null
        var artist: String? = null
        var duration: String? = null

        init {
            // 음악 목록의 음악을 클릭했을 때
            binding.root.setOnClickListener {
                val context = binding.root.context
                val playActivityIntent = Intent(context, PlayActivity::class.java)

                playActivityIntent.putExtra("albumUri", this.albumUri)
                playActivityIntent.putExtra("title", this.title)
                playActivityIntent.putExtra("artist", this.artist)
                playActivityIntent.putExtra("duration", this.duration)

                context.startActivity(playActivityIntent)
            }
        }

        fun setMusic(item: MusicListItem) {
            binding.run {
                imageViewAlbumArt.setImageURI(item.getAlbumUri())
                textViewMusicTitle.text = item.title
                textViewArtist.text = item.artist

                val duration = SimpleDateFormat("mm:ss").format(item.duration)
                textViewDuration.text = duration
            }

            this.musicUri = item.getMusicUri()
            this.albumUri = item.getAlbumUri()
            this.title = item.title
            this.artist = item.artist
            this.duration = SimpleDateFormat("mm:ss").format(item.duration)
        }
    }
}