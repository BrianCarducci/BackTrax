package com.example.backtrax

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_player.*
import java.lang.Thread.sleep

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

        val songTitleTextView = view.findViewById<TextView>(R.id.song_title_text_view)
        songTitleTextView.text = songFileNames[songIndex!!].substringBefore(".")

        val speedSpinner = view.findViewById<Spinner>(R.id.speed_spinner)
        val spinnerArrayAdapter = ArrayAdapter<String>(activity, R.layout.speed_spinner_item,
            listOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x"))
        speedSpinner.adapter = spinnerArrayAdapter

        speedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        speedSpinner.setSelection(3)

        val playPauseButton = view.findViewById<ImageButton>(R.id.play_pause_button)
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                playPauseButton.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_play, activity?.theme))
            } else {
                playPauseButton.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_pause, activity?.theme))
            }
            activityCallback?.playPauseSong(isPlaying)
            isPlaying = !isPlaying
        }

        val seekBar = view.findViewById<SeekBar>(R.id.seek_bar)
        seekBar.max = songData[SONG_DURATION]!!/1000
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    activityCallback?.seekBarChanged(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        val nextButton = view.findViewById<ImageButton>(R.id.next_button)
        nextButton.setOnClickListener {
            playPauseButton.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_pause, activity?.theme))
            songData = activityCallback?.nextSong()!!
            songTitleTextView.text = songFileNames[songData[SONG_INDEX]!!]
            seekBar.max = songData[SONG_DURATION]!!/1000
        }

        val prevButton = view.findViewById<ImageButton>(R.id.prev_button)
        prevButton.setOnClickListener {
            songData = activityCallback?.prevSong()!!
            songTitleTextView.text = songFileNames[songData[SONG_INDEX]!!]
            seekBar.max = songData[SONG_DURATION]!!/1000
        }

//        Thread(object: Runnable {
//            override fun run() {
//                try {
//                    Log.d("threadrunning", "threadrunning")
//                    Thread.sleep(1000)
//                } catch (e: InterruptedException) {
//                    Log.d("exceptionz", e.message)
//                }
//            }
//        }).start()
//        activity?.runOnUiThread {
//            Log.d("runningthread", "runningthread")
//            sleep(1000)
//        }

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


    interface OnPlayerFragmentInteractionListener {
        // TODO: Update argument type and name
        fun playPauseSong(isPlaying: Boolean)
        fun nextSong(): HashMap<String, Int>
        fun prevSong(): HashMap<String, Int>
        fun songTick()
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
