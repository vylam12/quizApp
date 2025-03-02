package com.example.myapplication.services
import com.example.myapplication.repositori.TranslateResponse
import com.example.myapplication.utils.FindVocabularyRequest
import com.example.myapplication.utils.SaveVocabularyRequest
import com.example.myapplication.utils.TranslateRequest
import retrofit2.Response
import retrofit2.http.*

interface TranslateService {
   @POST("findVocabulary")
  suspend fun findVocabulary(@Body request: FindVocabularyRequest): TranslateResponse.FindVocabulary

   @POST("saveVocabulary")
   suspend fun saveVocabulary(@Body request: SaveVocabularyRequest): Response<TranslateResponse.SaveVocabulary>

   @POST("translate")
   suspend fun translate(@Body request: TranslateRequest): Response<TranslateResponse.Translate>
}
