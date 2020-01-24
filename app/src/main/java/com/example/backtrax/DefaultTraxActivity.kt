package com.example.backtrax

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_default_trax.*


class DefaultTraxActivity : AppCompatActivity() {
    var mediaPlayer = MediaPlayer()
    var isPlayerLooping = false
    var playbackSpeed = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_trax)

        val files = assets.list("mp3s").toMutableList()
        val iterate = files.listIterator()
        while (iterate.hasNext()) {
            iterate.set(iterate.next().substringBefore((".")))
        }

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.songs_listview_item, files)
        songs_list_view.apply {
            adapter = arrayAdapter
        }
        songs_list_view.setOnItemClickListener { parent, view, position, id ->
            val element = arrayAdapter.getItem(position)
            val afd = assets.openFd("mp3s/$element.mp3")
            mediaPlayer.reset()
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mediaPlayer.prepare()
            mediaPlayer.isLooping = isPlayerLooping
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(playbackSpeed))
            mediaPlayer.start()
        }

        val spinnerArrayAdapter = ArrayAdapter<String>(this, R.layout.speed_spinner_item,
            arrayOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x"))
        speed_spinner.apply {
            adapter = spinnerArrayAdapter
        }
        speed_spinner.setSelection(3)
        speed_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val element = spinnerArrayAdapter.getItem(position)
                playbackSpeed = (element.substringBefore("x") + "f").toFloat()
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.setPlaybackParams(
                        mediaPlayer.getPlaybackParams()
                            .setSpeed(playbackSpeed)
                    )
                }
            }
        }

    }

    fun playOrPause(view: View) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            play_button.text = "play"
        } else {
            mediaPlayer.setPlaybackParams(
                mediaPlayer.getPlaybackParams()
                    .setSpeed(playbackSpeed)
            )
            mediaPlayer.start()
            play_button.text = "pause"
        }
    }

    fun stop(view: View) {
        mediaPlayer.reset()
    }

    fun setPlayerLooping(view: View) {
        isPlayerLooping = !mediaPlayer.isLooping
        mediaPlayer.isLooping = !mediaPlayer.isLooping
    }

    fun nextSong(view: View) {

    }

    fun prevSong(view: View) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}
