package com.example.myapplication.utils

import com.example.myapplication.model.Vocabulary

data class FindVocabularyRequest(
    val userId: String,
    val word: String
)
data class DeleteVocabularyRequest(
    val idUserVocabulary: String
)

data class SaveVocabularyRequest(
    val userId: String,
    val vocabulary:Vocabulary
)
data class TranslateRequest(
    val text: String
)
data class SaveUserVocabularyRequest(
    val userId: String,
    val idVocab: String
)

