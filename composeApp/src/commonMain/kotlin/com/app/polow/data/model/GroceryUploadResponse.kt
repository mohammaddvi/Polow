package com.app.polow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroceryItemDto(
    val id: Int,
    val name: String
)

@Serializable
data class GroceryUploadResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("upload_id")
    val uploadId: Int? = null,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("grocery-items")
    val groceryItems: List<GroceryItemDto>,
    @SerialName("count")
    val count: Int,
    @SerialName("provider")
    val provider: String,
    @SerialName("message")
    val message: String,
    @SerialName("created_at")
    val createdAt: String? = null
)


