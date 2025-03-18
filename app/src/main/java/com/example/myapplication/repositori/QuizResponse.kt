package com.example.myapplication.repositori

import com.example.myapplication.model.Question


sealed class QuizResponse {
    data class GenerateQuiz(val message:String, val quizId:String): AuthResponse()
    data class GetQuiz(val listQuestion: List<Question>)
    data class UpdateQuiz(val message:String)
}