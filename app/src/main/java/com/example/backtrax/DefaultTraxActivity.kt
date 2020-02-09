package com.example.backtrax

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_default_trax.*
import java.util.ArrayList


class DefaultTraxActivity :
    AppCompatActivity(),
    SongListFragment.OnSongListFragmentInteractionListener,
    PlayerFragment.OnPlayerFragmentInteractionListener {
    private var mediaPlayer = MediaPlayer()
    private var isPlayerLooping = false
    private var playbackSpeed = 1.0f
    private var songFileNames = mutableListOf<String>()
    private var currentSongIndex = 0

    private val songListFragment = SongListFragment()
    private val playerFragment = PlayerFragment()

    private val handler = Handler()
    private var runnable: Runnable? = null
    private var thread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_trax)

        songFileNames = assets?.list("mp3s")!!.toMutableList()
        val iterate = songFileNames.listIterator()
        while (iterate.hasNext()) {
            iterate.set(iterate.next().substringBefore("."))
        }

        mediaPlayer.setOnCompletionListener {

        }

        val bundle = Bundle()
        bundle.putStringArrayList("songFileNames", songFileNames as ArrayList<String>)
        songListFragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.song_list_fragment_container, songListFragment)
        fragmentTransaction.commit()

        runnable = Runnable {
            if (mediaPlayer.isPlaying && playerFragment.isVisible) {
                playerFragment.progressBarTick(mediaPlayer.currentPosition)
            }
            handler.postDelayed(runnable, 1000)
        }

        runOnUiThread(runnable)

//        runOnUiThread(object : Runnable {
//            override fun run() {
//                if (mediaPlayer.isPlaying && playerFragment.isVisible) {
//                    playerFragment.progressBarTick(mediaPlayer.currentPosition)
//                }
//
//                handler.postDelayed(this, 1000)
//            }
//        })
    }

    private fun setUpMediaPlayer(afd: AssetFileDescriptor) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        mediaPlayer.prepare()
        mediaPlayer.isLooping = isPlayerLooping
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(playbackSpeed))
    }

    override fun songListViewFragmentClicked(songTitle: String?, songIndex: Int) {
        currentSongIndex = songIndex

        val afd = assets.openFd("mp3s/${songTitle}.mp3")
        setUpMediaPlayer(afd)

        val bundle = Bundle()
        bundle.putInt("songIndex", songIndex)
        bundle.putInt("songDuration", mediaPlayer.duration)
        playerFragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.song_list_fragment_container, playerFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        mediaPlayer.start()
    }

    override fun playPauseSong(isPlaying: Boolean) {
        if (isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    override fun nextSong(): HashMap<String, Int> {
        if (currentSongIndex < songFileNames.size - 1) {
            currentSongIndex++
        } else {
            currentSongIndex = 0
        }
        val afd = assets.openFd("mp3s/${songFileNames[currentSongIndex]}.mp3")
        setUpMediaPlayer(afd)
        mediaPlayer.start()
        return hashMapOf("songIndex" to currentSongIndex, "songDuration" to mediaPlayer.duration)
    }

    override fun prevSong(): HashMap<String, Int> {
        if (mediaPlayer.currentPosition >= 3000) {
            mediaPlayer.seekTo(0)
        } else {
            if (currentSongIndex > 0) {
                currentSongIndex--
            } else {
                currentSongIndex = songFileNames.size - 1
            }
            val afd = assets.openFd("mp3s/${songFileNames[currentSongIndex]}.mp3")
            setUpMediaPlayer(afd)
            mediaPlayer.start()
        }
        return hashMapOf("songIndex" to currentSongIndex, "songDuration" to mediaPlayer.duration)
    }

    override fun speedSpinnerClicked(speed: String) {
        playbackSpeed = (speed.substringBefore("x") + "f").toFloat()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.setPlaybackParams(
                mediaPlayer.getPlaybackParams()
                    .setSpeed(playbackSpeed)
            )
        }
    }

    override fun songTick(): Int {
        return mediaPlayer.currentPosition
    }

    override fun seekBarChanged(progress: Int) {
        mediaPlayer.seekTo(progress * 1000)
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        Log.d("destroyed", "destroyeddddd")
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer.release()
    }

}

