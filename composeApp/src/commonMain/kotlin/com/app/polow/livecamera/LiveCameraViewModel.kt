package com.app.polow.livecamera

import com.app.polow.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// CameraScanViewModel.kt - commonMain
class LiveCameraViewModel : BaseViewModel() {
    val cameraManager = CameraManager()

    private val _uiState = MutableStateFlow(LiveCameraUiState())
    val uiState: StateFlow<LiveCameraUiState> = _uiState.asStateFlow()

    private val _capturedImage = MutableStateFlow<ByteArray?>(null)
    val capturedImage: StateFlow<ByteArray?> = _capturedImage.asStateFlow()

    fun startCamera() {
        if (cameraManager.hasPermission()) {
            _uiState.value = _uiState.value.copy(
                isCameraActive = true,
                permissionGranted = true
            )
        } else {
            _uiState.value = _uiState.value.copy(permissionGranted = false)
            cameraManager.requestPermission()
        }
    }

    fun stopCamera() {
        _uiState.value = _uiState.value.copy(isCameraActive = false)
    }

    fun capturePhoto() {
        _uiState.value = _uiState.value.copy(isCapturing = true)

        cameraManager.capturePhoto { imageData ->
            _capturedImage.value = imageData
            _uiState.value = _uiState.value.copy(
                isCapturing = false,
                captureSuccess = imageData != null
            )
        }
    }

    fun onPermissionGranted() {
        _uiState.value = _uiState.value.copy(permissionGranted = true)
        startCamera()
    }

    override fun onCleared() {
        super.onCleared()
        stopCamera()
    }
}