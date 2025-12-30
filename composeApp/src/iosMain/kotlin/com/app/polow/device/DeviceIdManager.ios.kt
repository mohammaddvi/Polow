package com.app.polow.device

import platform.UIKit.UIDevice
import platform.Foundation.NSUUID

actual class DeviceIdManager {
    actual fun getDeviceId(): String {
        val device = UIDevice.currentDevice
        val identifier = device.identifierForVendor
        
        // identifierForVendor returns a unique ID for the device per vendor
        // It persists across app installs as long as at least one app from the vendor is installed
        // If nil (rare case), generate a UUID
        return identifier?.UUIDString ?: NSUUID().UUIDString
    }
}


