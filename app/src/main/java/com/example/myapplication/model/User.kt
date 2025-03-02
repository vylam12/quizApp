package com.example.myapplication.model
data class User(
    val id: String,
    val email: String,
    val fullname: String,
    val resetOtp: String? = null,
    val otpExpires: String? = null,
    val createdAt: String,
    val updatedAt:String
)
