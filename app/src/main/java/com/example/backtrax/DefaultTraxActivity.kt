package com.example.backtrax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class DefaultTraxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_trax)

        val testArray = arrayOf("hi", "bye","hi", "bye","hi", "bye","hi", "bye","hi", "bye","hi", "bye","hi", "bye","hi", "bye")

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.song_in_listview, testArray)
        findViewById<ListView>(R.id.songs_list_view).apply {
            adapter = arrayAdapter
        }
    }


}
