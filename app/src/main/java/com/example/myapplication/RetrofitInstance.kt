package com.example.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Base URL'i Ticketmaster API'si için belirtin
    private const val BASE_URL = "https://app.ticketmaster.com/"

    // Retrofit instance'ını oluşturuyoruz
    val api: TicketmasterApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Base URL
            .addConverterFactory(GsonConverterFactory.create()) // JSON verisini Gson ile dönüştür
            .build()
            .create(TicketmasterApi::class.java) // API arayüzünü oluştur
    }
}
