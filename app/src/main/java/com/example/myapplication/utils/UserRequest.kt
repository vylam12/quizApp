package com.example.myapplication.utils
data class GetIdUserRequest(
    val userId:String
)

data class ChangePasswordRequest(
    val idToken: String,
    val newPassword: String
)

