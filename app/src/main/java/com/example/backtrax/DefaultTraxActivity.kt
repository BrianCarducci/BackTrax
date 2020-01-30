package com.example.backtrax

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_default_trax.*


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_trax)

        songFileNames = assets.list("mp3s").toMutableList()
        val iterate = songFileNames.listIterator()
        while (iterate.hasNext()) {
            iterate.set(iterate.next().substringBefore((".")))
        }

        val bundle = Bundle()
        bundle.putStringArrayList("song_file_names", songFileNames as ArrayList<String>)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        songListFragment.arguments = bundle
        fragmentTransaction.add(R.id.song_list_fragment_container, songListFragment)
        fragmentTransaction.commit()

    }

    private fun setUpAndStartMediaPlayer(afd: AssetFileDescriptor) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        mediaPlayer.prepare()
        mediaPlayer.isLooping = isPlayerLooping
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(playbackSpeed))
        mediaPlayer.start()
    }

    override fun songListViewFragmentClicked(songTitle: String?, songIndex: Int) {
        currentSongIndex = songIndex
        val bundle = Bundle()
        bundle.putInt("songIndex", songIndex)
        playerFragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.song_list_fragment_container, playerFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        val afd = assets.openFd("mp3s/${songTitle}.mp3")
        setUpAndStartMediaPlayer(afd)
    }

    override fun playPauseSong(isPlaying: Boolean) {
        if (isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    override fun nextSong() {
        if (currentSongIndex < songFileNames.size - 1) {
            currentSongIndex++
        } else {
            currentSongIndex = 0
        }
        val afd = assets.openFd("mp3s/${songFileNames[currentSongIndex]}.mp3")
        setUpAndStartMediaPlayer(afd)
    }

    override fun prevSong() {
        if (mediaPlayer.currentPosition >= 3000) {
            mediaPlayer.seekTo(0)
        } else {
            if (currentSongIndex > 0) {
                currentSongIndex--
            } else {
                currentSongIndex = songFileNames.size - 1
            }
            val afd = assets.openFd("mp3s/${songFileNames[currentSongIndex]}.mp3")
            setUpAndStartMediaPlayer(afd)
        }
    }



//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_default_trax)

//        songFileNames = assets.list("mp3s").toMutableList()
//        val iterate = songFileNames.listIterator()
//        while (iterate.hasNext()) {
//            iterate.set(iterate.next().substringBefore((".")))
//        }
//
//        val arrayAdapter = ArrayAdapter<String>(this, R.layout.songs_listview_item, songFileNames)
//        songs_list_view.apply {
//            adapter = arrayAdapter
//        }
//        songs_list_view.setOnItemClickListener { parent, view, position, id ->
//            val states = view.drawableState
//            arrayAdapter.notifyDataSetChanged()
//            view.setSelected(true)
//
//            Log.d("viewselected", view.isSelected.toString())
//            val element = arrayAdapter.getItem(position)
//            now_playing_text_view.text = element
//            currentSongIndex = position
//            val afd = assets.openFd("mp3s/$element.mp3")
//            setUpAndStartMediaPlayer(afd)
//        }
//
//        val spinnerArrayAdapter = ArrayAdapter<String>(this, R.layout.speed_spinner_item,
//            arrayOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x"))
//        speed_spinner.apply {
//            adapter = spinnerArrayAdapter
//        }
//        speed_spinner.setSelection(3)
//        speed_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val element = spinnerArrayAdapter.getItem(position)
//                playbackSpeed = (element?.substringBefore("x") + "f").toFloat()
//                if (mediaPlayer.isPlaying) {
//                    mediaPlayer.setPlaybackParams(
//                        mediaPlayer.getPlaybackParams()
//                            .setSpeed(playbackSpeed)
//                    )
//                }
//            }
//        }

//    }

//
//    fun playOrPause(view: View) {
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.pause()
//            play_button.text = "play"
//        } else {
//            mediaPlayer.setPlaybackParams(
//                mediaPlayer.getPlaybackParams()
//                    .setSpeed(playbackSpeed)
//            )
//            mediaPlayer.start()
//            play_button.text = "pause"
//        }
//    }
//
//    fun stop(view: View) {
//        mediaPlayer.reset()
//    }
//
//    fun setPlayerLooping(view: View) {
//        isPlayerLooping = !mediaPlayer.isLooping
//        mediaPlayer.isLooping = !mediaPlayer.isLooping
//    }
//
//    fun nextSong(view: View) {
//        if (currentSongIndex == songFileNames.size - 1) {
//            currentSongIndex = 0
//
//        } else {
//            currentSongIndex++
//        }
//        songs_list_view.performItemClick(
//            songs_list_view.adapter.getView(currentSongIndex, null, null),
//            currentSongIndex,
//            songs_list_view.adapter.getItemId(currentSongIndex)
//        )
//    }
//
//    fun prevSong(view: View) {
//        if (currentSongIndex == 0) {
//            currentSongIndex = songFileNames.size - 1
//        } else {
//            currentSongIndex--
//        }
//        songs_list_view.performItemClick(
//            songs_list_view.adapter.getView(currentSongIndex, null, null),
//            currentSongIndex,
//            songs_list_view.adapter.getItemId(currentSongIndex)
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.release()
//    }

}
