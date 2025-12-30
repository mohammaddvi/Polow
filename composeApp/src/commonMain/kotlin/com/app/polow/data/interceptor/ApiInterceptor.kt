package com.app.polow.data.interceptor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Custom API interceptor for logging requests and responses
 * This interceptor logs request/response details for all API calls
 * 
 * Usage: Wrap your HttpClient calls or use with HttpClient extension
 */
object ApiInterceptor {
    
    /**
     * Logs request details before sending
     */
    fun logRequest(method: HttpMethod, url: String, headers: Headers) {
        val headersString = headers.entries().joinToString("\n") { "${it.key}: ${it.value}" }
        
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("📤 API REQUEST")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("Method: $method")
        println("URL: $url")
        if (headersString.isNotEmpty()) {
            println("Headers:")
            println(headersString)
        }
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
    
    /**
     * Logs response details after receiving
     */
    suspend fun logResponse(response: HttpResponse) {
        val status = response.status
        val url = response.call.request.url.toString()
        val headers = response.headers.entries().joinToString("\n") { "${it.key}: ${it.value}" }
        
        // Try to read body for logging (this will be done in API service to avoid consuming)
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("📥 API RESPONSE")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("URL: $url")
        println("Status: ${status.value} ${status.description}")
        if (headers.isNotEmpty()) {
            println("Headers:")
            println(headers)
        }
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
}
