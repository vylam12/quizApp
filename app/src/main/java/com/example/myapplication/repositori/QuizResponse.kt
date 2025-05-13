package com.example.myapplication.repositori

import com.example.myapplication.model.Flashcard
import com.example.myapplication.model.ProgressData
import com.example.myapplication.model.Question
import com.example.myapplication.model.Quiz


sealed class QuizResponse {
    data class GenerateQuiz(val canStartQuiz:Boolean, val quizId:String): AuthResponse()
    data class GetHistoryQuiz(val listQuiz: List<Quiz>)
    data class GetQuiz(val listQuestion: List<Question>)
    data class UpdateQuiz(val message:String)
    data class updateAfterFlashcard(val message:String)
    data class GetLenghtQuiz(val totalQuiz:Int, val totalPoint: Int)
    data class GetProgress(val message:String, val data: ProgressData)
    data class ChechUserVocabulart(val canStartQuiz: Boolean,val message: String? = null)
    data class GetVocab(val data: List<Flashcard>, val canMakeFlashcard:Boolean)
    data class GetQuestion(val data: List<Question>, val canMakeFlashcard:Boolean)



}