package com.example.myapplication.model

data class FriendsInvitation(
    val id: String,
    val id_receiver: String,
    val id_sender: String ,
    val status: String,
    val createdAt: String,
    val updatedAt:String
)