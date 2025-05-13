package com.example.myapplication.services

import com.example.myapplication.repositori.TranslateResponse
import com.example.myapplication.utils.DeleteVocabularyRequest
import com.example.myapplication.utils.FindVocabularyRequest
import com.example.myapplication.utils.SaveUserVocabularyRequest
import com.example.myapplication.utils.SaveVocabularyRequest
import com.example.myapplication.utils.TranslateRequest
import retrofit2.Response
import retrofit2.http.*

interface TranslateService {

    @POST("findVocabulary")
    suspend fun findVocabulary(@Body request: FindVocabularyRequest): TranslateResponse.FindVocabulary

    @POST("deleteVocabulary")
    suspend fun deleteVocabulary(@Body request: DeleteVocabularyRequest)
    : Response<TranslateResponse.DeleteVocabulary>

    // này lưu trong tin nhắn _ đầu vào là thông tin chứ ko phải id
//    @POST("saveVocabulary")
//    suspend fun saveVocabulary(@Body request: SaveVocabRequest): Response<TranslateResponse.SaveVocabulary>

    @POST("saveVocabulary")
    suspend fun saveVocabularyById(
        @Body request: SaveUserVocabularyRequest
    ): Response<TranslateResponse.SaveVocabulary>

    @POST("saveVocabulary")
    suspend fun saveVocabularyByData(
        @Body request: SaveVocabularyRequest
    ): Response<TranslateResponse.SaveVocabulary>

    @POST("translate")
    suspend fun translate(@Body request: TranslateRequest): Response<TranslateResponse.Translate>

    @GET("get-list-saveVocab/{userId}")
    suspend fun getListSaveVocab(@Path("userId") userId: String): Response<TranslateResponse.GetListSaveVocab>

    @GET("get-list-vocab/{userId}")
    suspend fun getListVocab(@Path("userId") userId: String): Response<TranslateResponse.GetListVocab>
}
