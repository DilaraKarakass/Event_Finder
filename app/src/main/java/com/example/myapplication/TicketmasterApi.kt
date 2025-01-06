package com.example.myapplication

import TicketmasterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterApi {

    // Örnek GET isteği, şehir ve kategoriye göre etkinlikleri alır
    @GET("v2/events.json") // Ticketmaster API endpoint'ini burada belirtin
    fun getEvents(
        @Query("apikey") apiKey: String,
        @Query("city") city: String,
        @Query("category") category: String?
    ): Call<TicketmasterResponse>
}
