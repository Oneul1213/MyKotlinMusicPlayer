package Oneul1213.mykotlinmusicplayer

import android.graphics.Bitmap

data class MusicListItem(
    val albumArt: Bitmap?,
    val title: String,
    val singer: String)
