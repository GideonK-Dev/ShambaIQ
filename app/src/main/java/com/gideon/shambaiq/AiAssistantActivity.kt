package com.gideon.shambaiq

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiAssistantActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = ArrayList<ChatMessage>()
    private lateinit var rvChatHistory: RecyclerView
    private lateinit var etMessageInput: EditText

    private val client = OkHttpClient()

    // ⚠️ PASTE YOUR BRAND NEW GENERATED KEY HERE INSIDE THE QUOTES
    private val apiKey = "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"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        rvChatHistory = findViewById(R.id.rvChatHistory)
        etMessageInput = findViewById(R.id.etMessageInput)
        val btnSendMessage = findViewById<ImageButton>(R.id.btnSendMessage)

        chatAdapter = ChatAdapter(messageList)
        rvChatHistory.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rvChatHistory.adapter = chatAdapter

        loadDefaultGreetings()

        findViewById<ImageButton>(R.id.btnBackChat).setOnClickListener { finish() }

        btnSendMessage.setOnClickListener {
            val queryText = etMessageInput.text.toString().trim()
            if (queryText.isNotEmpty()) {
                sendMessageToOpenAI(queryText)
            }
        }
    }

    private fun loadDefaultGreetings() {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        // Clear out any old elements to guarantee a pristine blank slate interface canvas
        messageList.clear()

        // This sets the clean, welcoming intro message from your AI assistant
        messageList.add(ChatMessage(
            "Hello! 🌾 I am your ShambaIQ AI Farm Assistant. How can I help you with your crops, livestock, or farm management today?",
            false,
            currentTime
        ))

        chatAdapter.notifyDataSetChanged()
    }

    private fun sendMessageToOpenAI(userMessage: String) {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        messageList.add(ChatMessage(userMessage, true, currentTime))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        rvChatHistory.smoothScrollToPosition(messageList.size - 1)
        etMessageInput.text.clear()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-4o")
            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are an agronomy specialist AI. Keep responses straightforward, practical, actionable, and structured cleanly for smallholder farmers.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userMessage)
                })
            }
            put("messages", messagesArray)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AiAssistantActivity, "Connection error.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@AiAssistantActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val responseString = response.body?.string()
                    if (responseString != null) {
                        val jsonResponse = JSONObject(responseString)
                        val choices = jsonResponse.getJSONArray("choices")
                        val aiReply = choices.getJSONObject(0).getJSONObject("message").getString("content")

                        runOnUiThread {
                            val responseTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                            messageList.add(ChatMessage(aiReply.trim(), false, responseTime))
                            chatAdapter.notifyItemInserted(messageList.size - 1)
                            rvChatHistory.smoothScrollToPosition(messageList.size - 1)
                        }
                    }
                }
            }
        })
    }
}