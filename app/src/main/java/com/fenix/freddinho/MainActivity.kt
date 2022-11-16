package com.fenix.freddinho
import Dependent
import HttpHelper
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity(override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()) :
        AppCompatActivity(), CoroutineScope {

    private lateinit var dialog: AlertDialog
    private var job: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val preferences = getPreferences(MODE_PRIVATE)

            val userName = preferences.getString("userName", "admin@admin.com.br")
                    .toString()
            val password = preferences.getString("userPass", "admin")
                    .toString()

            launch{
                val dependent: List<Dependent> = callApi(userName, password);

                dependent.map {
                    createProfile(it)
                }
            }
        }
        catch (e: Exception) {
            Toast.makeText(applicationContext,
                    e.message,
                    Toast.LENGTH_LONG)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
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

    private fun callApi(userName: String, password: String): List<Dependent>{
        val helper = HttpHelper()
        val userId = helper.getUserId(userName, password)

        return helper.getDependentByUserId("1")
    }

    private fun createProfile(dependent: Dependent){
        val build = AlertDialog.Builder(this)

        val view: View = if (dependent.gender == 'F'){
            layoutInflater.inflate(R.layout.profile_dialog1, null)
        }else{
            layoutInflater.inflate(R.layout.profile_dialog2, null)
        }

        build.setView(view)

        val imgButton = view.findViewById<Button>(R.id.btn_profile)
        imgButton.setOnClickListener {
            val i = Intent(this, ChatAdult::class.java)
            startActivity(i)
        }

        val nameView = view.findViewById<TextView>(R.id.user_name)
        nameView.text = dependent.name

        dialog = build.create()
        dialog.show()
    }
}