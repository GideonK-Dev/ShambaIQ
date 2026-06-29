package com.gideon.shambaiq

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Dynamic greeting setup
        val tvUserGreeting = findViewById<TextView>(R.id.tvUserGreeting)
        val prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val savedUsername = prefs.getString("username", "Farmer")
        tvUserGreeting.text = "Hello, $savedUsername 🖐"

        // 2. Scan Crop redirection feature panel
        val btnScanCrop = findViewById<RelativeLayout>(R.id.btnScanCrop)
        btnScanCrop.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }

        // 3. AI Assistant chat messenger workspace button interaction
        val btnAiAssistant = findViewById<androidx.cardview.widget.CardView>(R.id.cardAiAssistant)
        btnAiAssistant.setOnClickListener {
            val intent = Intent(this, AiAssistantActivity::class.java)
            startActivity(intent)
        }
    }
}