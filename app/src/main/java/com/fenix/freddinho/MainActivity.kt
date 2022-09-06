package com.fenix.freddinho

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_profile_girl).setOnClickListener {
            showDialogNormal()
        }

        val btnProfileBoy = findViewById<Button>(R.id.btn_profile_boy)

        btnProfileBoy.setOnClickListener {
            val a = Intent(this, ChatChild::class.java)
            startActivity(a)
        }
    }

    private fun showDialogNormal(){
        val build = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.alert_dialog, null)

        build.setView(view)

        val btnCancelar = view.findViewById<Button>(R.id.btn_cancelar)
        btnCancelar.setOnClickListener { dialog.dismiss() }

        val btnContinuar = view.findViewById<Button>(R.id.btn_comecar)
        btnContinuar.setOnClickListener {
            val i = Intent(this, ChatAdult::class.java)
            startActivity(i)
        }

        dialog = build.create()
        dialog.show()
    }

}