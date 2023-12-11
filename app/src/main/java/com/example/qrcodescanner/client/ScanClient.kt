package com.example.qrcodescanner.client

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object responsible for providing an instance of the Retrofit client to interact with the ScanApi.
 */
object ScanClient {

    // Retrofit instance for network communication
    private var retrofit: Retrofit? = null
    // Gson instance with custom date format for JSON parsing
    var gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()

    /**
     * Provides a singleton instance of the [ScanApi] interface using Retrofit.
     * @return An instance of the [ScanApi] interface.
     */
    fun getClient(): ScanApi {
        // Create a Retrofit instance if it doesn't exist
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://31cb-14-232-123-15.ngrok-free.app/")  // Base URL for the API
                .addConverterFactory(GsonConverterFactory.create(gson)) // Gson converter for JSON parsing
                .build()
        }
        // Return the created or existing instance of the ScanApi interface
        return retrofit!!.create(ScanApi::class.java)
    }
}
