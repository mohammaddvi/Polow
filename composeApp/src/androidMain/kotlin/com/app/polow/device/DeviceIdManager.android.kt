package com.app.polow.device

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import java.util.UUID

actual class DeviceIdManager {
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context.applicationContext
    }
    
    actual fun getDeviceId(): String {
        val ctx = context ?: throw IllegalStateException(
            "DeviceIdManager not initialized. Call initialize(context) first."
        )
        
        return getOrCreateDeviceId(ctx)
    }
    
    private fun getOrCreateDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        val storedId = prefs.getString("device_id", null)
        
        if (storedId != null) {
            return storedId
        }
        
        // Try to use Android ID first
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        
        val deviceId = if (androidId != null && androidId.isNotEmpty() && androidId != "9774d56d682e549c") {
            // Use Android ID if it's valid (not the emulator default)
            androidId
        } else {
            // Generate a UUID if Android ID is not available or is the emulator default
            UUID.randomUUID().toString()
        }
        
        // Store it for future use
        prefs.edit().putString("device_id", deviceId).apply()
        
        return deviceId
    }
}

