package com.example.myapplication.services

import com.example.myapplication.repositori.QuizResponse
import com.example.myapplication.utils.FlashcardRequest
import com.example.myapplication.utils.updateResultQuizRequest
import retrofit2.Response
import retrofit2.http.*

interface QuizService {
    @GET("generate-quiz/{userId}")
    suspend fun generateQuiz(@Path("userId") userId: String): Response<QuizResponse.GenerateQuiz>

    @GET("get-history-quiz/{userId}")
    suspend fun getHistoryQuiz(@Path("userId") userId: String): Response<QuizResponse.GetHistoryQuiz>

    @GET("get-quiz/{quizId}")
    suspend fun getQuiz(@Path("quizId") quizId: String): Response<QuizResponse.GetQuiz>

    @POST("update-result-quiz")
    suspend fun updateQuizResult(@Body request: updateResultQuizRequest): Response<QuizResponse.UpdateQuiz>

    @POST("check-user-vocabulary")
    suspend fun checkUserVocabulary(@Body request: Map<String, String>): Response<QuizResponse.ChechUserVocabulart>

    @GET("getVocab/{userId}")
    suspend fun getVocab(@Path("userId") userId: String): Response<QuizResponse.GetVocab>

    @GET("flashcard-review/{userId}")
    suspend fun getQuestion(@Path("userId") userId: String): Response<QuizResponse.GetQuestion>

    @GET("get-lenght-quiz/{userId}")
    suspend fun getLeghtQuiz(@Path("userId") userId: String): Response<QuizResponse.GetLenghtQuiz>

    @GET("progress/{userId}")
    suspend fun getProgress(@Path("userId") userId: String): Response<QuizResponse.GetProgress>

    @POST("update-after-flashcard")
    suspend fun updateAfterFlashcard(@Body request: FlashcardRequest): Response<QuizResponse.updateAfterFlashcard>
}