package com.example.myapplication.services

import com.example.myapplication.repositori.QuizResponse
import com.example.myapplication.utils.generateQuizRequest
import com.example.myapplication.utils.updateResultQuizRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizService {
    @POST("generate-quiz")
    suspend fun generateQuiz(@Body request: generateQuizRequest): Response<QuizResponse.GenerateQuiz>

    @GET("get-quiz/{quizId}")
    suspend fun getQuiz(@Path("quizId") quizId: String): Response<QuizResponse.GetQuiz>

    @POST("update-result-quiz")
    suspend fun updateQuizResult(@Body request: updateResultQuizRequest): Response<QuizResponse.UpdateQuiz>
}