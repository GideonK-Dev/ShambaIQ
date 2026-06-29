package com.gideon.shambaiq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val items: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_USER = 1
    private val TYPE_AI = 2

    override fun getItemViewType(position: Int): Int {
        return if (items[position].isUser) TYPE_USER else TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_item_message_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_item_message_ai, parent, false)
            AiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = items[position]
        if (holder is UserViewHolder) {
            holder.tvText.text = message.text
            holder.tvTime.text = message.timestamp
        } else if (holder is AiViewHolder) {
            holder.tvText.text = message.text
            holder.tvTime.text = message.timestamp
        }
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvText: TextView = v.findViewById(R.id.tvUserMessageText)
        val tvTime: TextView = v.findViewById(R.id.tvUserTimestamp)
    }

    class AiViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvText: TextView = v.findViewById(R.id.tvAiMessageText)
        val tvTime: TextView = v.findViewById(R.id.tvAiTimestamp)
    }
}