package com.example.myapplication.utils

data class FriendInvitedRequest(
    val idSender:String,
    val idReciver: String,
)
data class AcceptFriendInvitedRequest(
    val idSender:String,
    val idReciver: String,
    val status:String
)
data class UnFriendRequest(
    val idUser:String,
    val idFriend: String
)
