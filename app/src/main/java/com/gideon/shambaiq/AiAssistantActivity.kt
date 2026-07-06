package com.gideon.shambaiq

import android.os.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import java.text.SimpleDateFormat
import java.util.*

class AiAssistantActivity : AppCompatActivity() {
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter // This will turn black once the class is added below

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        val rv = findViewById<RecyclerView>(R.id.rvChatHistory)
        val edt = findViewById<EditText>(R.id.etMessageInput)
        val btn = findViewById<ImageButton>(R.id.btnSendMessage)

        adapter = ChatAdapter(messages) // This will turn black
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter


        // Greeting
        addMessage("Hello! I'm your ShambaIQ IA Farm Assistant. How can I help you with your crops, livestock, or farm management today?", false)

        btn.setOnClickListener {
            val text = edt.text.toString().trim()
            if (text.isNotEmpty()) {
                addMessage(text, true)
                edt.text.clear()

                val aiResponse = getLocalAIResponse(text)
                Handler(Looper.getMainLooper()).postDelayed({
                    addMessage(aiResponse, false)
                }, 500)
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        messages.add(ChatMessage(text, isUser, time))
        adapter.notifyItemInserted(messages.size - 1) // Error here will disappear
        findViewById<RecyclerView>(R.id.rvChatHistory).scrollToPosition(messages.size - 1)
    }

    private fun getLocalAIResponse(userMessage: String): String {
        val userInput = userMessage.lowercase().trim()
        var foundResponse = "I'm sorry, I don't recognize those symptoms."
        try {
            val inputStream = assets.open("farm_data.csv")
            val reader = inputStream.bufferedReader()
            reader.readLine()
            reader.forEachLine { line ->
                val parts = line.split("\",\"")
                if (parts.size >= 3) {
                    val keywords = parts[0].replace("\"", "").lowercase()
                    val responseText = parts[2].replace("\"", "")
                    val keywordsList = keywords.split(",")
                    for (key in keywordsList) {
                        if (userInput.contains(key.trim())) {
                            foundResponse = responseText
                            return@forEachLine
                        }
                    }
                }
            }
        } catch (e: Exception) {
            foundResponse = "Error: ${e.message}"
        }
        return foundResponse
    }

    // --- PLACE THE CLASS HERE, INSIDE CHATACTIVITY ---
    inner class ChatAdapter(private val list: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return if (list[position].isUser) 1 else 2
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val layout = if (viewType == 1) R.layout.activity_item_message_user else R.layout.activity_item_message_ai
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = list[position]
            // Use correct IDs based on the layout type
            if (message.isUser) {
                holder.itemView.findViewById<TextView>(R.id.tvUserMessageText).text = message.text
                holder.itemView.findViewById<TextView>(R.id.tvUserTimestamp).text = message.timestamp
            } else {
                holder.itemView.findViewById<TextView>(R.id.tvAiMessageText).text = message.text
                holder.itemView.findViewById<TextView>(R.id.tvAiTimestamp).text = message.timestamp
            }
        }

        override fun getItemCount(): Int = list.size
    }
}