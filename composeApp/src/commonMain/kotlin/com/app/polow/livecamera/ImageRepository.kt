package com.app.polow.livecamera
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ImageRepository {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun uploadImages(
        images: List<ImageData>,
        uploadUrl: String = "https://your-api.com/upload"
    ): Result<UploadResponse> {
        return try {
            val response = httpClient.submitFormWithBinaryData(
                url = uploadUrl,
                formData = formData {
                    images.forEachIndexed { index, image ->
                        append(
                            key = "images",
                            value = image.byteArray,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, image.mimeType)
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${image.name}\""
                                )
                            }
                        )
                    }
                }
            )

            val uploadResponse: UploadResponse = response.body()
            Result.success(uploadResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        httpClient.close()
    }
}