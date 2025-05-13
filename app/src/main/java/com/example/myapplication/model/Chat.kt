package com.example.myapplication.model

data class Chat(
    val chatId: String,
    val participants: List<String>,
    val unreadCount: Int,
    val lastMessage: String?,
    val participantsInfo: List<UserChat> = emptyList(),
    val messages: List<Message> = emptyList()
)

data class UserChat(
    val id: String = "",
    val fullname: String = "",
    val avatar: String = ""
)