package com.gideon.shambaiq

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONArray
import org.json.JSONObject

class ApiHelper(var context: Context) {

    // POST
    fun post(api: String, params: RequestParams) {
        Toast.makeText(context, "Please wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)

        client.post(api, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                Toast.makeText(context, "Response: $response", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                val errMsg = errorResponse?.optString("message") ?: throwable?.message ?: "Unknown Error"
                Toast.makeText(context, "Error: $errMsg", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Requires Access Token - Authentication Route
    fun post_login(api: String, params: RequestParams) {
        Toast.makeText(context, "Please wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)

        client.post(api, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                val message = response?.optString("message") ?: ""

                // 🚀 FIXED: Robust check handles both "Login success" and "Login successful" safely
                if (message.contains("Login success", ignoreCase = true)) {
                    val user = response?.optJSONObject("user")
                    val username = user?.optString("username") ?: ""
                    val email = user?.optString("email") ?: ""

                    // 🔐 Save session details to SharedPreferences
                    val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putString("username", username)
                    editor.putString("email", email)
                    editor.apply()

                    Toast.makeText(context, "Welcome $username", Toast.LENGTH_LONG).show()

                    // 🗺️ Redirect to MainActivity / Dashboard Home Screen
                    val intent = Intent(context, Home::class.java)
                    // Clear the background backstack so pressing the hardware back button won't return to the login screen
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                } else {
                    // Displays alternative error alert strings directly from your database backend
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                // Safeguards errors if backend throws structural exceptions
                val errMsg = errorResponse?.optString("message") ?: throwable?.message ?: "Login Failed"
                Toast.makeText(context, "Error: $errMsg", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun loadProducts(url: String, recyclerView: RecyclerView, progressBar: ProgressBar? = null) {
        progressBar?.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val client = AsyncHttpClient(true, 80, 443)

        client.get(context, url, null, "application/json", object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONArray
            ) {
                progressBar?.visibility = View.GONE
                // val productList = ProductAdapter.fromJsonArray(response)
                // val adapter = ProductAdapter(productList)
                // recyclerView.adapter = adapter
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                progressBar?.visibility = View.GONE
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // GET
    fun get(api: String, callBack: CallBack) {
        val client = AsyncHttpClient(true, 80, 443)
        client.get(context, api, null, "application/json", object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONArray
            ) {
                callBack.onSuccess(response)
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                callBack.onSuccess(response)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                callBack.onFailure(errorResponse?.toString() ?: throwable?.message)
            }
        })
    }

    // PUT
    fun put(api: String, jsonData: JSONObject) {
        Toast.makeText(context, "Please Wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)
        val con_body = StringEntity(jsonData.toString())

        client.put(context, api, con_body, "application/json", object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                Toast.makeText(context, "Response $response ", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                Toast.makeText(
                    context,
                    "Error Occurred: " + (errorResponse?.toString() ?: throwable?.toString()),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // DELETE
    fun delete(api: String, jsonData: JSONObject) {
        Toast.makeText(context, "Please Wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)
        val con_body = StringEntity(jsonData.toString())

        client.delete(context, api, con_body, "application/json", object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                Toast.makeText(context, "Response $response ", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                Toast.makeText(
                    context,
                    "Error Occurred: " + (errorResponse?.toString() ?: throwable?.toString()),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    interface CallBack {
        fun onSuccess(result: JSONArray?)
        fun onSuccess(result: JSONObject?)
        fun onFailure(result: String?)
    }
}