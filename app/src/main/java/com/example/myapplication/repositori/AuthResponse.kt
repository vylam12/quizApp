package com.example.myapplication.repositori

import com.example.myapplication.model.User

sealed class AuthResponse{
    data class Register(val message:String, val uid:String): AuthResponse()
    data class Login(val message: String, val token:String, val user:User): AuthResponse()
    data class ForgotPassword(val message: String): AuthResponse()
    data class VerifyOTP(val message: String, val token:String,val error: String?): AuthResponse()
    data class ResetPassword(val message: String): AuthResponse()
}