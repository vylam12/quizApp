package com.example.myapplication.utils

data class CreateChatRequest(
    val receiverId:String,
    val senderId: String,
    val content:String
)
data class SendMessageRequest(
    val chatId:String,
    val senderId: String,
    val content:String
)

data class CheckExistChatRequest(
    val senderId: String,
    val receiverId:String
)

data class DeleteRequest(
    val chatId:String
)
