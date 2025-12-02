package com.app.polow.livecamera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect class CameraManager() {
    fun capturePhoto(onResult: (ByteArray?) -> Unit)
    fun hasPermission(): Boolean
    fun requestPermission()
}

@Composable
expect fun CameraPreviewView(
    cameraManager: CameraManager,
    modifier: Modifier
)