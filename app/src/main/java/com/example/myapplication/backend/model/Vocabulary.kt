package com.example.myapplication.backend.model

data class Vocabulary(
    val word:String,
    val phonetic:String?, //phiên âm
    val meaning:String,
    val examples:List<String>,
   // val synonyms:List<String>,//từ đồng nghãi
)
