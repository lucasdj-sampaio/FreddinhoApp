package com.fenix.freddinho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ChatAdult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_adult)

        findViewById<Button>(R.id.btn_adult).setOnClickListener {
            val a = Intent(this, WatsonChat::class.java)
            startActivity(a)
        }
    }
}