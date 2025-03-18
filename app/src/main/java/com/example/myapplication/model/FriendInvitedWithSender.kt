package com.example.myapplication.model

data class FriendInvitedWithSender (
    val _id: String,
    val senderId: String,
    val receiverId: String,
    val status: String,
    val createdAt: String,
    val senderInfo: SenderInfo
)
data class SenderInfo(
    val _id: String,
    val fullname: String,
    val email: String
)