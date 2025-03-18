package com.example.myapplication.utils

data class updateResultQuizRequest(
    val quizId:String,
    val userId:String,
    val countCorrect:Int,
    val timeTaken:Double,
    val vocabularyResults : List<vocabulary>
)
data class vocabulary(
    val vocabId:String,
    val correctCount: Int,
    val wrongCount: Int
)
data class generateQuizRequest (
    val userId:String
)
