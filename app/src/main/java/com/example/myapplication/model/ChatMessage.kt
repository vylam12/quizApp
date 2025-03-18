package com.example.myapplication.model

data class Chat(
    val id: String,
    val participants: List<String>,
    val createdAt: String,
    val updatedAt:String
)
