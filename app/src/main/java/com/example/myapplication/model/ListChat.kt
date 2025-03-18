package com.example.myapplication.model

data class ListChat (
    val chatId: String,
    val participants: List<Participant>,
    val lastMessage: String?,
    val lastMessageTime: String?,
    val unreadCount: Int,
    val sender: Sender,
)
data class Participant(
    val id: String,
    val fullname: String,
    val avatar: String?
)
data class Sender (
    val id: String,
    val fullname: String,
    val avatar: String?
)



