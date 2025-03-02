package com.example.myapplication.utils

import com.example.myapplication.model.Meaning

data class FindVocabularyRequest(
    val word: String
)
data class SaveVocabularyRequest(
    val word: String,
    val phonetic:String? = null,
    val meanings: List<Meaning>
)
data class TranslateRequest(
    val text: String
)


