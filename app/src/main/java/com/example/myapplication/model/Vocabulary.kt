package com.example.myapplication.model

data class Vocabulary(
    val word:String,
    val phonetic:String? = null,
    val meanings:List<Meaning>,
    val createdAt: String,
    val updatedAt: String
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
