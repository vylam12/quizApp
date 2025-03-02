package com.example.myapplication.utils
data class LoginRequest(
    val idToken:String
)

data class RegisterRequest(
    val email:String,
    val password: String,
    val fullname:String
)
data class ForgotPasswordRequest(
    val email:String
)

data class VerifyOTPRequest(
    val email:String,
    val otp: String
)

data class ResetPasswordRequest(
    val token:String,
    val newPassword: String
)


