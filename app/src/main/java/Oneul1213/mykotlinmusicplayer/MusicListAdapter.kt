package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.MusicListItemBinding
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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

        init {
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "곡명 = ${binding.textViewMusicTitle.text}," +
                        " 가수 = ${binding.textViewArtist.text}", Toast.LENGTH_SHORT).show()
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
        }
    }
}