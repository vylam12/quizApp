package com.example.myapplication.model

import java.io.Serializable

data class ProgressData(
    val summary: Summary,
    val completedQuizzes: List<CompletedQuiz>,
    val learnedWords: List<LearnedWord>
)
data class Summary(
    val learnedWordsCount: Int,
    val lastReviewedAt: String?,
    val totalLearningDays: Int
)

data class CompletedQuiz(
    val _id: String,
    val totalQuestion: Int,
    val countCorrect: Int,
    val timeTaken: Double,
    val createdAt: String
): Serializable

data class LearnedWord(
    val word: String,
    val isKnown: Boolean,
    val flashcardViews: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val lastReviewedAt: String
) : Serializable