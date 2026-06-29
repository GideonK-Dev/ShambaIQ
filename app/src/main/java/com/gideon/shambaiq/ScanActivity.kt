package com.gideon.shambaiq

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null // Holds the live session for flash control
    private lateinit var cameraExecutor: ExecutorService

    private var isFlashOn = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK // Supports switching lenses

    // 🖼️ Gallery Selection Contract handler
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(this, "Image loaded from gallery!", Toast.LENGTH_SHORT).show()
            // TODO: Route URI path straight to API helper upload block
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        viewFinder = findViewById(R.id.viewFinder)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 1. Kick off custom UI animations
        startScanningAnimation()

        // 2. Start Camera Engine
        startCameraXEngine()

        // 3. FIXED: Listens to 'tabGallery' TextView instead of the old 'btnOpenGallery' LinearLayout
        findViewById<TextView>(R.id.tabGallery).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 4. Close/Back Button
        findViewById<ImageButton>(R.id.btnCloseScan).setOnClickListener {
            finish()
        }

        // 5. Hardware Capture Button Shutter Trigger
        findViewById<View>(R.id.btnCapturePhoto).setOnClickListener {
            takePhotoAction()
        }

        // 6. NEW: Hardware Flash Toggle control integration
        findViewById<ImageButton>(R.id.btnToggleFlash).setOnClickListener { view ->
            toggleFlashControl(view as ImageButton)
        }

        // 7. NEW: Front/Rear Lens Switch control integration
        findViewById<ImageButton>(R.id.btnFlipCamera).setOnClickListener {
            flipCameraLens()
        }
    }

    private fun startScanningAnimation() {
        val scanLine = findViewById<View>(R.id.viewScanningLine)
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.95f
        ).apply {
            duration = 2000
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }
        scanLine.startAnimation(animation)
    }

    private fun startCameraXEngine() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()

            // Dynamic camera lens selector
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                // Capture active camera handle control back to instance variable scope
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Toast.makeText(this, "Failed to load device camera engine.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun toggleFlashControl(flashButton: ImageButton) {
        val cameraControl = camera?.cameraControl
        val cameraInfo = camera?.cameraInfo

        if (cameraControl != null && cameraInfo != null && cameraInfo.hasFlashUnit()) {
            isFlashOn = !isFlashOn
            cameraControl.enableTorch(isFlashOn) // Turns hardware flash/torch on or off

            // 🚀 FIXED: Swaps system drawables cleanly based on the hardware state
            if (isFlashOn) {
                // Flash is ON: Switch icon to a solid/active indicator
                flashButton.setImageResource(android.R.drawable.btn_star_big_on)
                flashButton.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light))
                Toast.makeText(this, "Flash activated", Toast.LENGTH_SHORT).show()
            } else {
                // Flash is OFF: Switch icon back to the default clear/off indicator
                flashButton.setImageResource(android.R.drawable.btn_star_big_off)
                flashButton.clearColorFilter()
                Toast.makeText(this, "Flash deactivated", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Flash unavailable or lens orientation unsupported.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun flipCameraLens() {
        // Toggle lens configuration between front and back camera setups safely
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        // Re-bind configurations back into camera lifecycle stack dynamically
        startCameraXEngine()
    }

    private fun takePhotoAction() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(outputDirectory, "shamba_scan_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(baseContext, "Crop captured successfully!", Toast.LENGTH_SHORT).show()
                    // Pass photoFile path directly to your backend AI model analysis pipeline here
                }
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(baseContext, "Error saving scan photo.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private val outputDirectory: File by lazy {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}