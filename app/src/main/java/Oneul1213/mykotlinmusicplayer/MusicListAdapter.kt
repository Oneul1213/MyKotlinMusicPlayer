package Oneul1213.mykotlinmusicplayer

import Oneul1213.mykotlinmusicplayer.databinding.MusicListItemBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MusicListAdapter() : RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
    var data = mutableListOf<MusicListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MusicListItemBinding.inflate(LayoutInflater.from(parent.context),
                                                                parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int  = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(val binding: MusicListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "곡명 = ${binding.textViewMusicTitle.text}," +
                        " 가수 = ${binding.textViewSinger.text}", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(item: MusicListItem) {
            binding.imageViewAlbumArt.setImageBitmap(item.albumArt)
            binding.textViewMusicTitle.text = item.title
            binding.textViewSinger.text = item.singer
        }
    }
}