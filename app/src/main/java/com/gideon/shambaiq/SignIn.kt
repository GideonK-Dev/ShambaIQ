package com.gideon.shambaiq

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gideon.shambaiq.ApiHelper
import com.gideon.shambaiq.R
import com.loopj.android.http.RequestParams




class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val signIn=findViewById<TextView>(R.id.signin_link)

        signIn.setOnClickListener {
            val signInLink= Intent(applicationContext, SignUp::class.java)
            startActivity(signInLink)
        }
//        find the edittext button by id
        val email=findViewById<EditText>(R.id.email)
        val password=findViewById<EditText>(R.id.password)
        val signInn=findViewById<Button>(R.id.signin)
//lovendro
        signInn.setOnClickListener {
            val api="https://gideonk.alwaysdata.net/api/signin"

//            request param is the container used to collect the user details it's like form data in js

            val data= RequestParams()

            data.put("email",email.text.toString())
            data.put("password",password.text.toString().trim())


//        Api helper-it deliver our data to the api

            val helper= ApiHelper(applicationContext)
            helper.post_login(api,data)

//            val signInn= Intent(applicationContext,signInn::class.java)
//            startActivity(signInn)
        }



//        https://gideonk.alwaysdata.net/api/signin
//        https://gideonk.alwaysdata.net/api/signup
//        https://gideonk.alwaysdata.net/api/getproductdetails
//        https://gideonk.alwaysdata.net/api/mpesa_payment

    }
}