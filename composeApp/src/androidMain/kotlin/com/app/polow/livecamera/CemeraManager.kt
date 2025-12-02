package com.app.polow.livecamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import java.io.File

actual class CameraManager {
    internal lateinit var controller: LifecycleCameraController
    internal lateinit var context: Context

    actual fun capturePhoto(onResult: (ByteArray?) -> Unit) {
        if (!::controller.isInitialized || !::context.isInitialized) {
            onResult(null)
            return
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            File(context.cacheDir, "image.jpg")
        ).build()

        controller.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bytes = context.contentResolver.openInputStream(output.savedUri!!)?.readBytes()
                    onResult(bytes)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraManager", "Photo capture failed: ${exception.message}", exception)
                    onResult(null)
                }
            }
        )
    }

    actual fun hasPermission(): Boolean {
        if (!::context.isInitialized) return false
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun requestPermission() {
        // Not implemented on Android side, handled in UI
    }
}
