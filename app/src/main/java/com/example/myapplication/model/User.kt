package com.example.myapplication.model
data class User(
    val _id: String,
    val id: String,
    val email: String,
    val fullname: String,
    val avatar: String,
    val gender: String,
    val birthDay: String,
    val resetOtp: String? = null,
    val otpExpires: String? = null,
    val createdAt: String,
    val updatedAt:String
)
