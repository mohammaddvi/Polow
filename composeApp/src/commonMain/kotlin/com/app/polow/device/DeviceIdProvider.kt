package com.app.polow.device

// Singleton instance for easy access
// For Android: Initialize with context using DeviceIdProvider.initialize(DeviceIdManager().apply { initialize(context) })
// For iOS: Works without initialization
object DeviceIdProvider {
    private var deviceIdManager: DeviceIdManager? = null
    
    fun initialize(manager: DeviceIdManager) {
        deviceIdManager = manager
    }
    
    fun getDeviceId(): String {
        // For iOS, DeviceIdManager works without initialization
        // For Android, it should be initialized with context before first use
        val manager = deviceIdManager ?: DeviceIdManager()
        if (deviceIdManager == null) {
            deviceIdManager = manager
        }
        return manager.getDeviceId()
    }
}

