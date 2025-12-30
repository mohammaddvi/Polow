package com.app.polow.data.api

import com.app.polow.data.config.ApiConfig
import com.app.polow.data.interceptor.ApiInterceptor
import com.app.polow.data.model.GroceryUploadResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

interface GroceryApiService {
    suspend fun uploadGroceryImage(
        imageData: ByteArray,
        deviceId: String,
    ): Result<GroceryUploadResponse>
}

class GroceryApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = ApiConfig.BASE_URL
) : GroceryApiService {

    override suspend fun uploadGroceryImage(
        imageData: ByteArray,
        deviceId: String,
    ): Result<GroceryUploadResponse> {
        return try {
            val url = "$baseUrl${ApiConfig.UPLOAD_GROCERIES_ENDPOINT}"
            
            // Build headers for logging
            val requestHeaders = Headers.build {
                append("device-id", deviceId)
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            
            // Log request using interceptor
            ApiInterceptor.logRequest(HttpMethod.Post, url, requestHeaders)
            
            val response = httpClient.submitFormWithBinaryData(
                url = url,
                formData = formData {
                    append(
                        key = "image",
                        value = imageData,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Image.JPEG)
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"grocery-image.jpg\""
                            )
                        }
                    )
                }
            ) {
                header("device-id", deviceId)
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }

            // Log response using interceptor
            ApiInterceptor.logResponse(response)
            
            // Check response status
            if (!response.status.isSuccess()) {
                val errorBody = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Unable to read error response"
                }
                logResponseBody(errorBody)
                return Result.failure(
                    Exception("API request failed with status ${response.status.value}: $errorBody")
                )
            }

            // Read response body as string first for debugging
            val responseBody = response.body<String>()
            
            // Log response body
            logResponseBody(responseBody)
            
            // Try to deserialize the response
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            }
            
            val uploadResponse: GroceryUploadResponse = try {
                json.decodeFromString(responseBody)
            } catch (e: Exception) {
                // If deserialization fails, throw with the actual response body for debugging
                throw Exception("Failed to deserialize response. Response body: $responseBody. Error: ${e.message}", e)
            }
            
            Result.success(uploadResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun logResponseBody(responseBody: String) {
        println("Body:")
        // Truncate very long responses for readability
        val truncatedBody = if (responseBody.length > 1000) {
            responseBody.take(1000) + "\n... [truncated ${responseBody.length - 1000} more characters]"
        } else {
            responseBody
        }
        println(truncatedBody)
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
}

