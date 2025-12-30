package com.app.polow.livecamera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.*
import platform.AVFoundation.*
import platform.CoreGraphics.CGRect
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.UIKit.UIView
import platform.darwin.NSObject

actual class CameraManager {
    private val captureSession = AVCaptureSession()
    private val photoOutput = AVCapturePhotoOutput()
    private var onCapture: ((NSData?) -> Unit)? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null

    // Internal function to update preview layer frame
    @OptIn(ExperimentalForeignApi::class)
    internal fun updatePreviewFrame(view: UIView) {
        previewLayer?.let { layer ->
            // Set frame directly from view bounds, same as in setupSession
            layer.frame = view.bounds
        }
    }

    // Internal function for the iOS implementation
    @OptIn(ExperimentalForeignApi::class)
    internal fun setupSession(previewView: UIView) {
        captureSession.sessionPreset = AVCaptureSessionPresetPhoto
        val discoverySession = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            AVMediaTypeVideo,
            AVCaptureDevicePositionBack
        )
        val captureDevice = discoverySession.devices.firstOrNull() as? AVCaptureDevice

        captureDevice?.let { device ->
            try {
                memScoped {
                    val errorVar = alloc<ObjCObjectVar<NSError?>>()
                    val input = AVCaptureDeviceInput.deviceInputWithDevice(device, errorVar.ptr)
                    val error = errorVar.value
                    if (error != null) {
                        println("Error creating AVCaptureDeviceInput: ${error.localizedDescription}")
                        return@let
                    }
                    input?.let { inputDevice ->
                        if (captureSession.canAddInput(inputDevice)) {
                            captureSession.addInput(inputDevice)
                        }
                    }
                }

                if (captureSession.canAddOutput(photoOutput)) {
                    captureSession.addOutput(photoOutput)
                }

                val previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)
                previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
                this.previewLayer = previewLayer
                previewView.layer.addSublayer(previewLayer)
                
                // Update frame on main thread after layout
                platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                    previewLayer.frame = previewView.bounds
                }

                captureSession.startRunning()
            } catch (e: Exception) {
                println("Error setting up camera: ${e.message}")
            }
        }
    }

    actual fun capturePhoto(onResult: (ByteArray?) -> Unit) {
        val settings = AVCapturePhotoSettings.photoSettingsWithFormat(mapOf(AVVideoCodecKey to AVVideoCodecTypeJPEG))
        onCapture = { nsData ->
            onResult(nsData?.toByteArray())
        }
        photoOutput.capturePhotoWithSettings(settings, PhotoCaptureDelegate(onCapture!!))
    }

    actual fun hasPermission(): Boolean {
        return when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> true
            else -> false
        }
    }

    actual fun requestPermission() {
        if (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) != AVAuthorizationStatusAuthorized) {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { _ ->
                // The result is handled by the UI by checking hasPermission() again.
            }
        }
    }
}

private class PhotoCaptureDelegate(private val onCapture: (NSData?) -> Unit) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
    override fun captureOutput(output: AVCapturePhotoOutput, didFinishProcessingPhoto: AVCapturePhoto, error: NSError?) {
        val photoData = didFinishProcessingPhoto.fileDataRepresentation()
        onCapture(photoData)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val bytes = this.bytes ?: return ByteArray(0)
    val length = this.length
    return ByteArray(length.toInt()).apply {
        (bytes as CPointer<ByteVar>).readBytes(length.toInt()).copyInto(this)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreviewView(
    cameraManager: CameraManager,
    modifier: Modifier,
) {
    val previewView = remember { UIView() }
    var isSessionSetup by remember { mutableStateOf(false) }

    // Set up the camera session only once
    LaunchedEffect(Unit) {
        if (!isSessionSetup) {
            cameraManager.setupSession(previewView)
            isSessionSetup = true
        }
    }

    UIKitView(
        factory = { 
            previewView.apply {
                // Ensure the view is properly configured
                backgroundColor = platform.UIKit.UIColor.clearColor
            }
        },
        modifier = modifier,
        onRelease = {
            // Clean up if needed
        }
    )
    
    // Update preview layer frame after view is laid out
    LaunchedEffect(previewView) {
        delay(200) // Wait for view to be laid out
        @OptIn(ExperimentalForeignApi::class)
        val bounds = previewView.bounds
        var shouldUpdate = false
        bounds.useContents {
            shouldUpdate = size.width > 0 && size.height > 0
        }
        if (shouldUpdate) {
            // Pass the view directly, same pattern as setupSession
            cameraManager.updatePreviewFrame(previewView)
        }
    }
}
