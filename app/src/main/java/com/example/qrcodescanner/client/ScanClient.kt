package com.example.qrcodescanner.client

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ScanClient {
    private var retrofit: Retrofit? = null
    var gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    fun getClient(): ScanApi {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://31cb-14-232-123-15.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!.create(ScanApi::class.java)
    }
}
