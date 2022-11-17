package com.fenix.freddinho

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ChatChild : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_child)

        val userName: String = intent?.getStringExtra("dependentName")
                .toString().uppercase(Locale.ROOT)

        val message = findViewById<TextView>(R.id.message)
        message.text = "OI, ${userName}!!\nQUE BOM QUE\n VOLTOU!!!"

        findViewById<Button>(R.id.btn_child).setOnClickListener {
            val a = Intent(this, WatsonChat::class.java)
            startActivity(a)
        }
    }
}