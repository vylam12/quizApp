package com.example.myapplication.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL="http://192.168.1.6:9000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy{ retrofit.create(AuthService::class.java)}
    val translateService: TranslateService by  lazy{ retrofit.create(TranslateService::class.java)}
}