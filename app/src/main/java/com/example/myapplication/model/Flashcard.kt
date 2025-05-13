package com.example.myapplication.model

data class Flashcard(
    val id: String,
    val word: String,
    val meaning: String,
    val phonetic: String,
    val audio: String,
    var isKnown: Boolean = false
)
