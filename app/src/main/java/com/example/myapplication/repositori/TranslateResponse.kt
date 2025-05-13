package com.example.myapplication.repositori

import com.example.myapplication.model.SavedVocab
import com.example.myapplication.model.Vocabulary

sealed class TranslateResponse {
    data class FindVocabulary(val userSaved: String,val newWord:Vocabulary):TranslateResponse()
    data class DeleteVocabulary(val userSaved: String):TranslateResponse()
    data class SaveVocabulary(val message: String,val newVocabulart:Vocabulary):TranslateResponse()
    data class Translate(val translation:String):TranslateResponse()
    data class GetListSaveVocab(val data:  List<SavedVocab>):TranslateResponse()
    data class GetListVocab(val data:  List<Vocab>):TranslateResponse()

    data class Vocab(
        val id: String,
        val word: String,
        val phonetic: String,
        val meaning: String,
        val audio:String,
        val isSaved: Boolean,
        val _idUserVocabulary: String
    )



}