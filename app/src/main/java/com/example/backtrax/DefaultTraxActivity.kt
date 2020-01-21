package com.example.backtrax

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_default_trax.*


//var mediaPlayer: MediaPlayer? = null
var mediaPlayer: MediaPlayer = MediaPlayer()
class DefaultTraxActivity : AppCompatActivity() {

    var isPlayerLooping = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_trax)

        val files = assets.list("mp3s").toMutableList()
        val iterate = files.listIterator()
        while (iterate.hasNext()) {
            iterate.set(iterate.next().substringBefore((".")))
        }
        Log.d("files array", files[0])

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.songs_listview_item, files)
        songsListView.apply {
            adapter = arrayAdapter
        }
        songsListView.setOnItemClickListener { parent, view, position, id ->
            val element = arrayAdapter.getItem(position)
            val afd = assets.openFd("mp3s/$element.mp3")
            Log.d("file info", "$afd")
            Log.d("islooping null", "${mediaPlayer.isLooping}")
            mediaPlayer.reset()
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            mediaPlayer.isLooping = isPlayerLooping
            mediaPlayer.start()
            Log.d("is playing", mediaPlayer.isPlaying.toString())
            Log.d("clicked file", afd.toString())
        }
    }

    fun stop(view: View) {
        mediaPlayer.reset()
    }

    fun loop(view: View) {
        isPlayerLooping = !mediaPlayer.isLooping
        mediaPlayer.isLooping = !mediaPlayer.isLooping
    }

}
