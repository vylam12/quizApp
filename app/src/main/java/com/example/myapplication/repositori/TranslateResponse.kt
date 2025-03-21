package com.example.myapplication.repositori

import com.example.myapplication.model.Vocabulary

sealed class TranslateResponse {
    data class FindVocabulary(val userSaved: String,val newWord:Vocabulary):TranslateResponse()
    data class DeleteVocabulary(val userSaved: String):TranslateResponse()
    data class SaveVocabulary(val message: String,val newVocabulart:Vocabulary):TranslateResponse()
    data class Translate(val translation:String):TranslateResponse()
}