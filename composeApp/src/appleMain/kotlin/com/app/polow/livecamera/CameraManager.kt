package com.app.polow.livecamera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.CValue
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

    fun setupSession(previewView: UIView) {
        captureSession.sessionPreset = AVCaptureSessionPresetPhoto
        val discoverySession = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            AVMediaTypeVideo,
            AVCaptureDevicePosition.AVCaptureDevicePositionBack
        )
        val captureDevice = discoverySession.devices.firstOrNull() as? AVCaptureDevice

        captureDevice?.let { device ->
            try {
                val input = AVCaptureDeviceInput(device)
                if (captureSession.canAddInput(input)) {
                    captureSession.addInput(input)
                }

                if (captureSession.canAddOutput(photoOutput)) {
                    captureSession.addOutput(photoOutput)
                }

                val previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)
                previewLayer.frame = previewView.layer.bounds
                previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
                previewView.layer.addSublayer(previewLayer)

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
                // The result of this permission request should be handled in the UI by checking hasPermission() again.
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

fun NSData.toByteArray(): ByteArray {
    val bytes = this.bytes ?: return ByteArray(0)
    val length = this.length
    return ByteArray(length.toInt()).apply {
        (bytes as kotlinx.cinterop.CPointer<kotlinx.cinterop.ByteVar>).readBytes(length.toInt()).copyInto(this)
    }
}

@Composable
actual fun CameraPreviewView(
    cameraManager: CameraManager,
    modifier: Modifier,
) {
    val previewView = remember { UIView() }
    cameraManager.setupSession(previewView)

    UIKitView(
        factory = { previewView },
        modifier = modifier
    )
}
