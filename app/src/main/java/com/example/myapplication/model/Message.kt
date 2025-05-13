package com.example.myapplication.model
import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val translatedContent: String = "",
    val isRead: Boolean ,
    val timestamp: Timestamp = Timestamp.now()
){
    constructor() : this("", "", "", false,  Timestamp.now())
}
