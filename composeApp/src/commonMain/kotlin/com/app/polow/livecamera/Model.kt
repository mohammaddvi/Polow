package com.app.polow.livecamera

import kotlinx.serialization.Serializable

data class ImageData(
    val byteArray: ByteArray,
    val name: String,
    val mimeType: String = "image/jpeg"
)

@Serializable
data class UploadResponse(
    val success: Boolean,
    val message: String,
    val imageUrls: List<String> = emptyList()
)

data class LiveCameraUiState(
    val isCameraActive: Boolean = false,
    val permissionGranted: Boolean = false,
    val isCapturing: Boolean = false,
    val captureSuccess: Boolean = false,
    val error: String? = null
)