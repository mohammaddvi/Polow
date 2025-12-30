package com.app.polow.domain.repository

import com.app.polow.domain.model.GroceryItem

interface GroceryRepository {
    suspend fun uploadGroceryImage(
        imageData: ByteArray,
        deviceId: String,
    ): Result<List<GroceryItem>>
}


