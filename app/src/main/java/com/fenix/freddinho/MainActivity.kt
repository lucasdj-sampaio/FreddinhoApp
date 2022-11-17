package com.fenix.freddinho
import Adapter
import Dependent
import HttpHelper
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity(override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()) :
        AppCompatActivity(), CoroutineScope {

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

            val viewList: RecyclerView = findViewById<RecyclerView>(R.id.profileList)
            viewList.layoutManager = LinearLayoutManager(this)
            viewList.setHasFixedSize(true)

            launch{
                val dependent: List<Dependent> = callApi(userName, password)

                createAdapter(dependent.toMutableList(), viewList)
            }
        }
        catch (e: Exception) {
            Toast.makeText(applicationContext,
                    e.message,
                    Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun callApi(userName: String, password: String): List<Dependent>{
        val helper = HttpHelper()
        val userId = helper.getUserId(userName, password)

        return helper.getDependentByUserId("1")
    }

    private fun createAdapter(dependentList: MutableList<Dependent>, viewList: RecyclerView){
        try{
            runOnUiThread {
                val responseAdapter = Adapter(this, dependentList)
                viewList.adapter = responseAdapter
            }
        }catch (e: Exception){
            Toast.makeText(applicationContext,
                    e.message,
                    Toast.LENGTH_LONG).show()
        }
    }
}