package com.fenix.freddinho
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.annotation.JacksonInject
import okhttp3.*
import java.io.IOException
import com.fasterxml.jackson.databind.ObjectMapper

class MainActivity : AppCompatActivity() {
    private lateinit var dialog: AlertDialog
    private val client = OkHttpClient();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getPreferences(MODE_PRIVATE);

        try {
            val userName = preferences.getString("userName", "").toString()
            val password = preferences.getString("userPass", "").toString()

            var userId = getUserId(userName, password).toInt();

            setContentView(R.layout.activity_main)
        }
        catch (e: Exception) {
            Toast.makeText(applicationContext,
                    "Falha ao realizar login: ${e.message}",
                    Toast.LENGTH_LONG)

            setContentView(R.layout.activity_login)
        }

        findViewById<Button>(R.id.btn_profile_girl).setOnClickListener {
            showDialogNormal()
        }

        val btnProfileBoy = findViewById<Button>(R.id.btn_profile_boy)

        btnProfileBoy.setOnClickListener {
            val a = Intent(this, ChatChild::class.java)
            startActivity(a)
        }
    }

    private fun getUserId(user: String, password: String): String{
        val request = Request.Builder().url("").build()
        var requestResponse = ""

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                requestResponse = e.message.toString()
            }

            override fun onResponse(call: Call, response: Response) {
                requestResponse = response.body()?.string().toString()
            }
        })

        return requestResponse;
    }

    private fun getDependentByUserId(userId: Int): Dependent{
        val request = Request.Builder().url("").build()
        val mapper = JacksonInject();


        return ;
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