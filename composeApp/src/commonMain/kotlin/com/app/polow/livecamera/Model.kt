package com.app.polow.livecamera

import com.app.polow.domain.model.GroceryItem

data class LiveCameraUiState(
    val isCameraActive: Boolean = false,
    val permissionGranted: Boolean = false,
    val isCapturing: Boolean = false,
    val captureSuccess: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null,
    val ingredients: List<GroceryItem> = emptyList(),
    val showIngredientsSheet: Boolean = false
)