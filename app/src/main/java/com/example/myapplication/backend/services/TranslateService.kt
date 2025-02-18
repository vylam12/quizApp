package com.example.myapplication.backend.services
import com.example.myapplication.backend.repositori.TranslateResponse
import retrofit2.Call
import retrofit2.http.*

interface TranslateService {

    @POST("translate")
    @FormUrlEncoded
    fun translate(
        @Field("q") text:String,
        @Field("source") sourceLang:String,
        @Field("target") targetLang:String,
        @Field("format") format:String = "text"

    ): Call<TranslateResponse>
}
