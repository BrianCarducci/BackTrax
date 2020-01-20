package com.example.backtrax

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_default_trax.*


var mediaPlayer: MediaPlayer? = null

class DefaultTraxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_trax)

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.song_in_listview, assets.list("mp3s"))
        songsListView.apply {
            adapter = arrayAdapter
        }
        songsListView.setOnItemClickListener { parent, view, position, id ->
            val element = arrayAdapter.getItem(position)
            val afd = assets.openFd("mp3s/yournightmp3.mp3")
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.yournightmp3)
            mediaPlayer?.start()
//            mediaPlayer?.reset()
//            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
//            mediaPlayer?.prepare()
//            mediaPlayer?.start()
            Log.d("clicked file", afd.toString())

        }
    }

    fun stop(view: View) {
        mediaPlayer?.reset()
    }



}
