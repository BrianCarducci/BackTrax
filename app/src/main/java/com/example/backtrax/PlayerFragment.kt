package com.example.backtrax

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_player.*
import org.w3c.dom.Text
import java.lang.Thread.sleep
import kotlin.math.max

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val SONG_TITLE = "songTitle"
private const val SONG_INDEX = "songIndex"
private const val SONG_DURATION = "songDuration"


class PlayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var songIndex: Int? = null
    private var songDuration: Int? = null
    private var activityCallback: OnPlayerFragmentInteractionListener? = null
    private var isPlaying = true
    private var songFileNames = mutableListOf<String>()
    private var songData = hashMapOf<String, Int>()
    private val handler = Handler()

    private var seekBar: SeekBar? = null
    private var songTitleTextView: TextView? = null
    private var songTimeTextView: TextView? = null
    private var songTotalTimeTextView: TextView? = null
    private var speedSpinner: Spinner? = null
    private var playPauseButton: ImageButton? = null
    private var nextButton: ImageButton? = null
    private var prevButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            songIndex = it.getInt(SONG_INDEX)
            songDuration = it.getInt(SONG_DURATION)
        }
        songData.put(SONG_INDEX, songIndex!!)
        songData.put(SONG_DURATION, songDuration!!)
        songFileNames = activity?.assets?.list("mp3s")!!.toMutableList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        songTitleTextView = view.findViewById(R.id.song_title_text_view)
        songTitleTextView?.let {
            it.text = songFileNames[songIndex!!].substringBefore(".")
        }

        songTotalTimeTextView = view.findViewById(R.id.song_total_time_text_view)
        songTotalTimeTextView?.let {
            it.text = calculateSongTimeString(songData[SONG_DURATION]!!)
        }

        songTimeTextView = view.findViewById(R.id.song_time_text_view)


        val spinnerArrayAdapter = ArrayAdapter<String>(activity as Context, R.layout.speed_spinner_item,
            listOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x"))
        speedSpinner = view.findViewById(R.id.speed_spinner)
        speedSpinner?.let {
            it.adapter = spinnerArrayAdapter
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val element = spinnerArrayAdapter.getItem(position)
                    activityCallback?.speedSpinnerClicked(element)
                }
            }
            it.setSelection(3)
        }

        playPauseButton = view.findViewById(R.id.play_pause_button)
        playPauseButton?.let { button ->
            button.setOnClickListener {
                if (isPlaying) {
                    button.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_play, activity?.theme))
                } else {
                    button.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_pause, activity?.theme))
                }
                activityCallback?.playPauseSong(isPlaying)
                isPlaying = !isPlaying
            }
        }

        seekBar = view.findViewById(R.id.seek_bar)
        seekBar?.let {seekBar ->
            seekBar.max = songData[SONG_DURATION]!!/1000
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        songTimeTextView?.let { textView ->
                            textView.text = calculateSongTimeString(progress * 1000)
                        }
                        activityCallback?.seekBarChanged(progress)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        nextButton = view.findViewById(R.id.next_button)
        nextButton?.let { button ->
            button.setOnClickListener {
                playPauseButton?.let {
                    it.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_pause, activity?.theme))
                }
                songData = activityCallback?.nextSong()!!
                songTitleTextView?.let { textView ->
                    textView.text = songFileNames[songData[SONG_INDEX]!!]
                }
                seekBar?.let { seekBar ->
                    seekBar.progress = 0
                    seekBar.max = songData[SONG_DURATION]!!/1000
                }
                songTotalTimeTextView?.let {textView ->
                    textView.text = calculateSongTimeString(songData[SONG_DURATION]!!)
                }
            }
        }

        prevButton = view.findViewById(R.id.prev_button)
        prevButton?.let { button ->
            button.setOnClickListener {
                songData = activityCallback?.prevSong()!!
                songTitleTextView?.let { textView ->
                    textView.text = songFileNames[songData[SONG_INDEX]!!]
                }
                seekBar?.let { seekBar ->
                    seekBar.progress = 0
                    seekBar.max = songData[SONG_DURATION]!!/1000
                }
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("attacheddd", "attacheddd")
        if (context is OnPlayerFragmentInteractionListener) {
            activityCallback = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        activityCallback = null
    }

    fun progressBarTick(currentPosition: Int) {
        seekBar?.let { seekBar ->
            seekBar.progress = currentPosition/1000
        }
        songTimeTextView?.let { textView ->
            textView.text = calculateSongTimeString(currentPosition)
        }
    }

    // Parameter should be in milliseconds
    private fun calculateSongTimeString(songDuration: Int): String {
        val songTimeString: String
        val songDurationMinutes = Math.floor(songDuration.toDouble()/60_000).toInt().toString()
        val songDurationLeftoverSeconds = songDuration / 1000 % 60
        if (songDurationLeftoverSeconds < 10) {
            songTimeString = "${songDurationMinutes}:0${songDurationLeftoverSeconds}"
        } else {
            songTimeString = "${songDurationMinutes}:${songDurationLeftoverSeconds}"
        }
        return songTimeString
    }

    interface OnPlayerFragmentInteractionListener {
        fun playPauseSong(isPlaying: Boolean)
        fun nextSong(): HashMap<String, Int>
        fun prevSong(): HashMap<String, Int>
        fun songTick(): Int
        fun seekBarChanged(progress: Int)
        fun speedSpinnerClicked(speed: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(SONG_TITLE, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
