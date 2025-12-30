package com.app.androidapp

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.androidapp.ui.theme.PolowTheme
import com.app.polow.device.DeviceIdManager
import com.app.polow.device.DeviceIdProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize DeviceIdManager with context for Android
        val deviceIdManager = DeviceIdManager()
        deviceIdManager.initialize(this)
        DeviceIdProvider.initialize(deviceIdManager)
        
        setContent {
            PolowTheme {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}