package com.example.myapplication.utils

import com.example.myapplication.model.Flashcard

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


data class  FlashcardRequest(
    val userId: String,
    val vocabList: List<VocabStatus>
)
data class VocabStatus(
    val vocabularyId: String,
    val isKnown: Boolean? =null
)