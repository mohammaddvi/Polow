package com.app.polow.data.di

import com.app.polow.data.api.GroceryApiService
import com.app.polow.data.api.GroceryApiServiceImpl
import com.app.polow.data.config.ApiConfig
import com.app.polow.data.repository.GroceryRepositoryImpl
import com.app.polow.domain.repository.GroceryRepository
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkModule {
    
    // Singleton HttpClient instance - should be reused across the app
    // Note: Request/response logging is handled in GroceryApiServiceImpl using ApiInterceptor helper
    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = false
                })
            }
        }
    }
    
    fun provideHttpClient(): HttpClient = httpClient
    
    fun provideGroceryApiService(
        httpClient: HttpClient = provideHttpClient(),
        baseUrl: String = ApiConfig.BASE_URL
    ): GroceryApiService {
        return GroceryApiServiceImpl(httpClient, baseUrl)
    }
    
    fun provideGroceryRepository(
        apiService: GroceryApiService = provideGroceryApiService()
    ): GroceryRepository {
        return GroceryRepositoryImpl(apiService)
    }
}

