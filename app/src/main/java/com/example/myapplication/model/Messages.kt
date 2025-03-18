package com.example.myapplication.model

data class Messages(
    val id: String,
    val content: String,
    val translatedContent:  String,
    val id_sender: String ,
    val chatId: String ,
    val isRead:Boolean,
    val createdAt: String,
    val updatedAt:String
)
