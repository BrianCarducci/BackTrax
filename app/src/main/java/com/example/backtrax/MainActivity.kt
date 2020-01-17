package com.example.backtrax

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startMyTraxActivity(view: View) {
        val intent = Intent(this, MyTraxActivity::class.java)
        startActivity(intent)
    }

    fun startCreateTraxActivity(view: View) {
        val intent = Intent(this, CreateTraxActivity::class.java)
        startActivity(intent)
    }

    fun startDefaultTraxActivity(view: View) {
        val intent = Intent(this, DefaultTraxActivity::class.java)
        startActivity(intent)
    }


}
