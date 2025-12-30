package com.app.polow.data.repository

import com.app.polow.data.api.GroceryApiService
import com.app.polow.domain.model.GroceryItem
import com.app.polow.domain.repository.GroceryRepository

class GroceryRepositoryImpl(
    private val apiService: GroceryApiService
) : GroceryRepository {

    override suspend fun uploadGroceryImage(
        imageData: ByteArray,
        deviceId: String,
    ): Result<List<GroceryItem>> {
        return apiService.uploadGroceryImage(imageData, deviceId)
            .map { response ->
                // Map DTOs to domain models
                response.groceryItems.map { dto ->
                    GroceryItem(
                        id = dto.id,
                        name = dto.name
                    )
                }
            }
    }
}


