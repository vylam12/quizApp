package com.example.myapplication.model

data class Vocabulary(
    val _id:String,
    val word:String,
    val phonetics:List<Phonetic>,
    val meanings:List<Meaning>,
    val quizAttempts:Int,
    val correctAnswers:Int,
    val createdAt: String,
    val updatedAt: String
)
data class Phonetic(
    val text: String,
    val audio: String,
    val type: String? = null,
    val license: License = License()
)
data class License(
    val name: String = "Không có",
    val url: String = "Không có"
)
data class Meaning(
    val type: String,
    val definitions: List<Definition>,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)
data class Definition(
    val definition: String,
    val example:String? = null
)
