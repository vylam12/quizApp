package com.example.myapplication.backend.services

import com.example.myapplication.backend.repositori.WordResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryService {
    @GET("api/v2/entries/en/{word}")
    fun getWordDenfinition(@Path("word") word:String): Call<List<WordResponse>>
}