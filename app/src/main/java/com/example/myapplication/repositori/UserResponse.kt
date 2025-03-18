package com.example.myapplication.repositori

sealed class UserResponse {
    data class GetIdUser(val userID: String): UserResponse()
    data class SaveFcmToken(val message: String): UserResponse()
    data class GetUserByName(val id: String,val fullname: String): UserResponse()
}
