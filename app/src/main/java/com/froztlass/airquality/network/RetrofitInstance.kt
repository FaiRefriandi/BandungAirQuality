package com.froztlass.airquality.network

import AirVisualService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.airvisual.com/v2/"
    const val API_KEY ="03a9e2a3-42ec-43b0-b852-2958a861153f"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AirVisualService by lazy {
        retrofit.create(AirVisualService::class.java)
    }
}
