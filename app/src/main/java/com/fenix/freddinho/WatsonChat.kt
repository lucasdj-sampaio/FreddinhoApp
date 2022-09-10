package com.fenix.freddinho

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper
import com.ibm.watson.assistant.v2.model.SessionResponse
import com.ibm.watson.speech_to_text.v1.SpeechToText
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import android.graphics.Typeface
import android.content.pm.PackageManager
import android.widget.Toast
import com.ibm.watson.assistant.v2.model.CreateSessionOptions
import android.net.ConnectivityManager
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibm.cloud.sdk.core.http.Response
import com.ibm.watson.assistant.v2.Assistant
import com.ibm.watson.assistant.v2.model.MessageInput
import com.ibm.watson.assistant.v2.model.MessageOptions
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults
import java.io.InputStream
import java.lang.Exception
import java.util.ArrayList

class WatsonChat : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ChatAdapter? = null
    private var messageArrayList: ArrayList<Message>? = null
    private var inputMessage: EditText? = null
    private var btnSend: ImageButton? = null
    private var btnRecord: ImageButton? = null
    private var initialRequest = false
    private var permissionToRecordAccepted = false
    private var listening = false
    private var capture: MicrophoneInputStream? = null
    private var mContext: Context? = null
    private var microphoneHelper: MicrophoneHelper? = null
    private var watsonAssistant: Assistant? = null
    private var watsonAssistantSession: Response<SessionResponse>? = null
    private var speechService: SpeechToText? = null

    private fun createServices() {
        watsonAssistant = Assistant(
            "2019-02-28", IamAuthenticator(
                mContext!!.getString(R.string.assistant_apikey)
            )
        )
        watsonAssistant!!.serviceUrl = mContext!!.getString(R.string.assistant_url)
        speechService = SpeechToText(IamAuthenticator(mContext!!.getString(R.string.STT_apikey)))
        speechService!!.serviceUrl = mContext!!.getString(R.string.STT_url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_watson)

        mContext = applicationContext
        inputMessage = findViewById(R.id.message)
        btnSend = findViewById(R.id.btn_send)
        btnRecord = findViewById(R.id.btn_record)

        val customFont = "Montserrat-Regular.ttf"
        val typeface = Typeface.createFromAsset(assets, customFont)

        inputMessage?.setTypeface(typeface)
        recyclerView = findViewById(R.id.recycler_view)
        messageArrayList = ArrayList<Message>()
        mAdapter = ChatAdapter(messageArrayList!!)
        microphoneHelper = MicrophoneHelper(this)

        val layoutManager = LinearLayoutManager(this)

        layoutManager.stackFromEnd = true
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setItemAnimator(DefaultItemAnimator())
        recyclerView?.setAdapter(mAdapter)
        inputMessage?.setText("")
        initialRequest = true

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        } else {
            Log.i(TAG, "Permission to record was already granted")
        }

        recyclerView?.addOnItemTouchListener(
            RecyclerTouchListener(
                applicationContext,
                recyclerView,
                object : ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val audioMessage = messageArrayList?.get(position) as Message
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        recordMessage()
                    }
                })
        )

        btnSend?.setOnClickListener(View.OnClickListener {
            if (checkInternetConnection()) {
                sendMessage()
            }
        })

        btnRecord?.setOnClickListener(View.OnClickListener { recordMessage() })
        createServices()
        sendMessage()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                finish()
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Speech-to-Text Record Audio permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted =
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            RECORD_REQUEST_CODE -> {
                if (grantResults.size == 0
                    || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
                return
            }
            MicrophoneHelper.REQUEST_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        // if (!permissionToRecordAccepted ) finish();
    }

    protected fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.RECORD_AUDIO),
            MicrophoneHelper.REQUEST_PERMISSION
        )
    }

    // Sending a message to Watson Assistant Service
    private fun sendMessage() {
        val inputmessage = inputMessage!!.text.toString().trim { it <= ' ' }

        if (!initialRequest) {
            val inputMessage = Message()
            inputMessage.message = inputmessage
            inputMessage.id = "1"
            messageArrayList!!.add(inputMessage)
        } else {
            val inputMessage = Message()
            inputMessage.message = inputmessage
            inputMessage.id = "100"
            initialRequest = false
            Toast.makeText(applicationContext, "Tap on the message for Voice", Toast.LENGTH_LONG)
                .show()
        }

        inputMessage!!.setText("")
        mAdapter!!.notifyDataSetChanged()

        val thread = Thread {
            try {
                if (watsonAssistantSession == null) {
                    val call = watsonAssistant!!.createSession(
                        CreateSessionOptions.Builder().assistantId(
                            mContext!!.getString(R.string.assistant_id)
                        ).build()
                    )
                    watsonAssistantSession = call.execute()
                }
                val input = MessageInput.Builder()
                    .text(inputmessage)
                    .build()
                val options = MessageOptions.Builder()
                    .assistantId(mContext!!.getString(R.string.assistant_id))
                    .input(input)
                    .sessionId(watsonAssistantSession!!.result.sessionId)
                    .build()
                val response = watsonAssistant!!.message(options).execute()
                Log.i(TAG, "run: " + response!!.result)
                if (response != null && response.result.output != null &&
                    !response.result.output.generic.isEmpty()
                ) {
                    val responses = response.result.output.generic
                    for (r in responses) {
                        var outMessage: Message
                        when (r.responseType()) {
                            "text" -> {
                                outMessage = Message()
                                outMessage.message = (r.text())
                                outMessage.id = "2"
                                messageArrayList!!.add(outMessage)
                            }
                            "option" -> {
                                outMessage = Message()
                                val title = r.title()
                                var OptionsOutput = ""
                                var i = 0
                                while (i < r.options().size) {
                                    val option = r.options()[i]
                                    OptionsOutput = """
                                          $OptionsOutput${option.label}
                                          
                                          """.trimIndent()
                                    i++
                                }
                                outMessage.message = """$title $OptionsOutput """.trimIndent()

                                outMessage.id = "2"
                                messageArrayList!!.add(outMessage)
                            }
                            "image" -> {
                                outMessage = Message(r)
                                messageArrayList!!.add(outMessage)
                            }
                            else -> Log.e("Error", "Unhandled message type")
                        }
                    }
                    runOnUiThread {
                        mAdapter!!.notifyDataSetChanged()
                        if (mAdapter!!.itemCount > 1) {
                            recyclerView!!.layoutManager!!
                                .smoothScrollToPosition(
                                    recyclerView,
                                    null,
                                    mAdapter!!.itemCount - 1
                                )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    //Record a message via Watson Speech to Text
    private fun recordMessage() {
        if (listening != true) {
            capture = microphoneHelper!!.getInputStream(true)
            Thread {
                try {
                    speechService!!.recognizeUsingWebSocket(
                        getRecognizeOptions(capture),
                        MicrophoneRecognizeDelegate()
                    )
                } catch (e: Exception) {
                    showError(e)
                }
            }.start()
            listening = true
            Toast.makeText(this@WatsonChat, "Listening....Click to Stop", Toast.LENGTH_LONG).show()
        } else {
            try {
                microphoneHelper!!.closeInputStream()
                listening = false
                Toast.makeText(
                    this@WatsonChat,
                    "Stopped Listening....Click to Start",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Check Internet Connection
     *
     * @return
     */
    private fun checkInternetConnection(): Boolean {
        // get Connectivity Manager object to check connection
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting

        // Check for network connections
        return if (isConnected) {
            true
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show()
            false
        }
    }

    //Private Methods - Speech to Text
    private fun getRecognizeOptions(audio: InputStream?): RecognizeOptions {
        return RecognizeOptions.Builder()
            .audio(audio)
            .contentType(ContentType.OPUS.toString())
            .model("en-US_BroadbandModel")
            .interimResults(true)
            .inactivityTimeout(2000)
            .build()
    }

    private fun showMicText(text: String) {
        runOnUiThread { inputMessage!!.setText(text) }
    }

    private fun enableMicButton() {
        runOnUiThread { btnRecord!!.isEnabled = true }
    }

    private fun showError(e: Exception) {
        runOnUiThread {
            Toast.makeText(this@WatsonChat, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    //Watson Speech to Text Methods.
    private inner class MicrophoneRecognizeDelegate : BaseRecognizeCallback() {
        override fun onTranscription(speechResults: SpeechRecognitionResults) {
            if (speechResults.results != null && !speechResults.results.isEmpty()) {
                val text = speechResults.results[0].alternatives[0].transcript
                showMicText(text)
            }
        }

        override fun onError(e: Exception) {
            showError(e)
            enableMicButton()
        }

        override fun onDisconnected() {
            enableMicButton()
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val TAG = "WatsonChat"
        private const val RECORD_REQUEST_CODE = 101
    }
}