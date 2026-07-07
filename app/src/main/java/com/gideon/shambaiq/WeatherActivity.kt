package com.gideon.shambaiq

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var tvLocationName: TextView
    private lateinit var tvWeatherCondition: TextView
    private lateinit var tvCurrentTemp: TextView
    private lateinit var tvFarmingAdvice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        findViewById<ImageButton>(R.id.btnBackWeather).setOnClickListener { finish() }

        tvLocationName = findViewById(R.id.tvLocationName)
        tvWeatherCondition = findViewById(R.id.tvWeatherCondition)
        tvCurrentTemp = findViewById(R.id.tvCurrentTemp)
        tvFarmingAdvice = findViewById(R.id.tvFarmingAdvice)

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if not granted yet
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1001
            )
        } else {
            // Permissions are granted, fetch coordinates
            getUserLiveLocation()
        }
    }

    private fun getUserLiveLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    // Get the true city name using Reverse Geocoding
                    val cityName = getCityNameFromCoordinates(lat, lon)

                    // Pass coordinates and city name to weather engine
                    fetchLiveWeatherData(lat, lon, cityName)
                } else {
                    // Fallback to Nairobi defaults if device location is switched off
                    fetchLiveWeatherData(-1.286389, 36.817223, "Nairobi, Kenya")
                }
            }
        } catch (e: SecurityException) {
            fetchLiveWeatherData(-1.286389, 36.817223, "Nairobi, Kenya")
        }
    }

    private fun getCityNameFromCoordinates(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                val country = address.countryName ?: "Kenya"
                "$city, $country"
            } else {
                "Current Location"
            }
        } catch (e: Exception) {
            "Current Location"
        }
    }

    private fun fetchLiveWeatherData(lat: Double, lon: Double, cityName: String) {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,rain,weather_code"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "Failed to load weather data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) return

                    val bodyString = response.body?.string()
                    if (bodyString != null) {
                        val json = JSONObject(bodyString)
                        val current = json.getJSONObject("current")

                        val temp = current.getDouble("temperature_2m")
                        val rain = current.getDouble("rain")
                        val weatherCode = current.getInt("weather_code")

                        runOnUiThread {
                            // Update dynamic location & temperature text blocks
                            tvLocationName.text = cityName
                            tvCurrentTemp.text = "${temp.toInt()}°C"

                            interpretWeatherStatus(weatherCode, rain)
                        }
                    }
                }
            }
        })
    }

    private fun interpretWeatherStatus(code: Int, rain: Double) {
        when {
            code >= 95 -> {
                tvWeatherCondition.text = "Heavy Thunderstorms"
                tvFarmingAdvice.text = "⛈️ Severe rainfall warning. Ensure drainage channels are clear around crop beds to prevent waterlogging or root rot."
            }
            rain > 0.0 || code in 51..67 || code in 80..86 -> {
                tvWeatherCondition.text = "Raining / Showers"
                tvFarmingAdvice.text = "🌧️ High soil moisture conditions present. Avoid applying chemical fertilizers (like top-dressing) or liquid insecticides today, as rain will wash them out before absorption."
            }
            code in 1..3 -> {
                tvWeatherCondition.text = "Partly Cloudy"
                tvFarmingAdvice.text = "⛅ Moderate conditions. Perfect timing for weeding, farm bed clearing, or soil layout inspections."
            }
            else -> {
                tvWeatherCondition.text = "Clear and Sunny"
                tvFarmingAdvice.text = "☀️ Dry weather window open. Ideal conditions for harvesting crops to prevent molding, or for manual crop transplantation and field watering rows."
            }
        }
    }

    // Handle user's choice when the location permission popup shows up
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLiveLocation()
        } else {
            // Permission Denied, load fallback Nairobi data
            fetchLiveWeatherData(-1.286389, 36.817223, "Nairobi, Kenya")
        }
    }
}