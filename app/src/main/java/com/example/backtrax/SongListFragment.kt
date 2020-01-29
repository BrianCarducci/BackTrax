package com.example.backtrax

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.fragment_song_list.*
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val SONG_FILE_NAMES = "song_file_names"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SongListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SongListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var activityCallback: OnSongListFragmentInteractionListener? = null
    private var songFileNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
        songFileNames = activity?.assets?.list("mp3s")!!.toMutableList()
        val iterate = songFileNames.listIterator()
        while (iterate.hasNext()) {
            iterate.set(iterate.next().substringBefore("."))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_list, container, false)

        val arrayAdapter = ArrayAdapter<String>(
            activity!!.applicationContext,
            R.layout.songs_listview_item,
            songFileNames
        )

        val songsListView = view.findViewById<ListView>(R.id.songs_list_view)
        songsListView.apply {
            adapter = arrayAdapter
        }
        songsListView.setOnItemClickListener { parent, view, position, id ->
            val songTitle = arrayAdapter.getItem(position)
            activityCallback?.songListViewFragmentClicked(songTitle)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongListFragmentInteractionListener) {
            activityCallback = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
       activityCallback = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    fun setOnSongListFragmentInteractionListener(callback: OnSongListFragmentInteractionListener) {
        this.activityCallback = callback
    }

    interface OnSongListFragmentInteractionListener {
        fun songListViewFragmentClicked(songTitle: String?)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SongListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SongListFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
