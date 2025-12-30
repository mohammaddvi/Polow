package com.app.polow.livecamera

import com.app.polow.BaseViewModel
import com.app.polow.data.di.NetworkModule
import com.app.polow.device.DeviceIdProvider
import com.app.polow.domain.model.GroceryItem
import com.app.polow.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LiveCameraViewModel(
    private val groceryRepository: GroceryRepository = NetworkModule.provideGroceryRepository()
) : BaseViewModel() {
    
    val cameraManager = CameraManager()

    private val _uiState = MutableStateFlow(LiveCameraUiState())
    val uiState: StateFlow<LiveCameraUiState> = _uiState.asStateFlow()

    private val _capturedImage = MutableStateFlow<ByteArray?>(null)
    val capturedImage: StateFlow<ByteArray?> = _capturedImage.asStateFlow()

    fun onCameraReady() {
        _uiState.value = _uiState.value.copy(isCameraActive = true)
    }

    fun onCameraStop() {
        _uiState.value = _uiState.value.copy(isCameraActive = false)
    }

    fun capturePhoto() {
        if (!_uiState.value.isCameraActive) return

        _uiState.value = _uiState.value.copy(isCapturing = true)

        cameraManager.capturePhoto { imageData ->
            _capturedImage.value = imageData
            _uiState.value = _uiState.value.copy(
                isCapturing = false,
                captureSuccess = imageData != null
            )
            
            // Automatically upload the image when captured
            imageData?.let { uploadImage(it) }
        }
    }

    fun uploadImage(imageData: ByteArray) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, error = null)
            
            val result = groceryRepository.uploadGroceryImage(
                imageData = imageData,
                deviceId = DeviceIdProvider.getDeviceId(),
            )
            
            result.fold(
                onSuccess = { ingredients ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        ingredients = ingredients,
                        showIngredientsSheet = true,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        error = error.message ?: "Failed to upload image"
                    )
                }
            )
        }
    }

    fun uploadImageFromGallery(imageData: ByteArray, gptProvider: String = "claude") {
        uploadImage(imageData)
    }

    fun addIngredient(name: String) {
        val newId = (_uiState.value.ingredients.maxOfOrNull { it.id } ?: 0) + 1
        val newIngredient = GroceryItem(id = newId, name = name)
        _uiState.value = _uiState.value.copy(
            ingredients = _uiState.value.ingredients + newIngredient
        )
    }

    fun removeIngredient(id: Int) {
        _uiState.value = _uiState.value.copy(
            ingredients = _uiState.value.ingredients.filter { it.id != id }
        )
    }

    fun showIngredientsSheet() {
        _uiState.value = _uiState.value.copy(showIngredientsSheet = true)
    }

    fun hideIngredientsSheet() {
        _uiState.value = _uiState.value.copy(showIngredientsSheet = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun onNext() {
        // Handle next action - could navigate to next screen
        hideIngredientsSheet()
    }

    override fun onCleared() {
        super.onCleared()
        onCameraStop()
    }
}
