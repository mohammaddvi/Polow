package com.app.polow.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val detail: String? = null
)


