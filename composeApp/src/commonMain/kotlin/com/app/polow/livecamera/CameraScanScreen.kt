package com.app.polow.livecamera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveCameraScanScreen(
    onNavigateBack: () -> Unit = {},
    onPhotoCaptured: (ByteArray) -> Unit = {},
    onGallerySelected: (List<ByteArray>) -> Unit = {}
) {
    val viewModel = remember { LiveCameraViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val capturedImage by viewModel.capturedImage.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var newIngredient by remember { mutableStateOf("") }

    // Handle permission and camera startup
    LaunchedEffect(Unit) {
        viewModel.startCamera()
    }

    // Handle cleanup
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.onCleared()
        }
    }

    // Handle captured image
    LaunchedEffect(capturedImage) {
        capturedImage?.let { image ->
            onPhotoCaptured(image)
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Multiple(maxSelection = 10),
        scope = coroutineScope,
        onResult = { byteArrays ->
            onGallerySelected(byteArrays)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            !uiState.permissionGranted -> {
                // Permission request screen
                PermissionRequestScreen(
                    onPermissionGranted = { viewModel.onPermissionGranted() }
                )
            }

            uiState.isCameraActive -> {
                // Live camera preview
                CameraPreviewView(
                    cameraManager = viewModel.cameraManager,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = "Starting camera...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "New recipe",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Bottom Controls
        if (uiState.isCameraActive) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Scan Button - This captures the photo
                Button(
                    onClick = {
                        if (!uiState.isCapturing) {
                            viewModel.capturePhoto()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isCapturing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = if (uiState.isCapturing) "Capturing..." else "📷 Scan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bottom Controls Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Button
                    TextButton(
                        onClick = { galleryLauncher.launch() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = Color(0xFF6B7280)
                            )
                            Text(
                                text = "Gallery",
                                color = Color(0xFF6B7280),
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Manual Ingredient Input
                    OutlinedTextField(
                        value = newIngredient,
                        onValueChange = { newIngredient = it },
                        placeholder = {
                            Text(
                                text = "type ingredient",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE91E63),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )

                    // Add Ingredient Button
                    IconButton(
                        onClick = {
                            if (newIngredient.isNotBlank()) {
                                // Add ingredient manually
                                newIngredient = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFE91E63),
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Capture Success Indicator
        if (uiState.captureSuccess) {
            LaunchedEffect(Unit) {
                // Flash effect or success animation
                kotlinx.coroutines.delay(200) // Brief flash effect
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Captured",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onPermissionGranted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Camera Permission Required",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "This app needs camera access to scan ingredients",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onPermissionGranted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                )
            ) {
                Text("Grant Permission")
            }
        }
    }
}

// Usage Example
@Composable
fun RecipeFlowScreen() {
    var currentScreen by remember { mutableStateOf("camera") }
    var capturedImageData by remember { mutableStateOf<ByteArray?>(null) }

    when (currentScreen) {
        "camera" -> {
            LiveCameraScanScreen(
                onNavigateBack = { /* navigate back */ },
                onPhotoCaptured = { imageData ->
                    capturedImageData = imageData
                    currentScreen = "ingredients"
                },
                onGallerySelected = { images ->
                    // Handle gallery selection
                    if (images.isNotEmpty()) {
                        capturedImageData = images.first()
                        currentScreen = "ingredients"
                    }
                }
            )
        }

        "ingredients" -> {
            // Your ingredients list screen
            // Pass capturedImageData to process
        }
    }
}
