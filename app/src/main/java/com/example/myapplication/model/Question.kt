package com.example.myapplication.model

data class Question (
    val id: String,
    val content: String,
    val vocabulary: VocabularyInfo,
    val correctAnswer: String,
    val options: List<String>,
)
data class VocabularyInfo(
    val _id: String,
    val meaning: String
)