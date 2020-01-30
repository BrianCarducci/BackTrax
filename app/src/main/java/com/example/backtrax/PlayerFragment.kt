package com.example.backtrax

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_player.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val SONG_TITLE = "songTitle"
private const val SONG_INDEX = "songIndex"


class PlayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var songTitle: String? = null
    private var songIndex: Int? = null
    private var activityCallback: OnPlayerFragmentInteractionListener? = null
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            songTitle = it.getString(SONG_TITLE)
            songIndex = it.getInt(SONG_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        val songTitleTextView = view.findViewById<TextView>(R.id.song_title_text_view)
        songTitleTextView.text = songTitle

        val speedSpinner = view.findViewById<Spinner>(R.id.speed_spinner)
        val spinnerArrayAdapter = ArrayAdapter<String>(activity, R.layout.speed_spinner_item,
            listOf("0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x"))
        speedSpinner.apply {
            adapter = spinnerArrayAdapter
        }
        speedSpinner.setSelection(3)
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

        val nextButton = view.findViewById<ImageButton>(R.id.next_button)
        nextButton.setOnClickListener {
            playPauseButton.setImageDrawable(resources.getDrawable(android.R.drawable.ic_media_pause, activity?.theme))
            activityCallback?.nextSong()
        }

        val prevButton = view.findViewById<ImageButton>(R.id.prev_button)
        prevButton.setOnClickListener {
            activityCallback?.prevSong()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        fun nextSong()
        fun prevSong()
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
