package com.gideon.shambaiq

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MarketPricesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_prices)

        // Close button navigation hook
        findViewById<ImageButton>(R.id.btnBackMarket).setOnClickListener {
            finish()
        }

        // Initialize the TextView references for the rows
        val tvCrop1Name = findViewById<TextView>(R.id.tvCrop1Name)
        val tvCrop1Price = findViewById<TextView>(R.id.tvCrop1Price)

        val tvCrop2Name = findViewById<TextView>(R.id.tvCrop2Name)
        val tvCrop2Price = findViewById<TextView>(R.id.tvCrop2Price)

        val tvCrop3Name = findViewById<TextView>(R.id.tvCrop3Name)
        val tvCrop3Price = findViewById<TextView>(R.id.tvCrop3Price)

        // Assign the precise hardcoded values matching your UI screenshot mock
        tvCrop1Name.text = "🌽  Maize"
        tvCrop1Price.text = "KSh 2,100 / 90kg"

        tvCrop2Name.text = "🫘  Beans"
        tvCrop2Price.text = "KSh 4,200 / 90kg"

        tvCrop3Name.text = "🍅  Tomatoes"
        tvCrop3Price.text = "KSh 1,500 / crate"

        val back= findViewById<ImageButton>(R.id.btnBackMarket)
        back.setOnClickListener {
            val intent= Intent(this, Home::class.java)
            startActivity(intent)
        }
    }
}