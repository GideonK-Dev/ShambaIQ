package com.gideon.shambaiq

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gideon.shambaiq.SignIn
import com.gideon.shambaiq.SignUp


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val tvSignIn = findViewById<TextView>(R.id.tvAlreadyHaveAccount)
        val btnSignUp = findViewById<Button>(R.id.btnGetStarted)


        tvSignIn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }


        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}