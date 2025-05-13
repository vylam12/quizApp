package com.example.myapplication.repositori

sealed class UserResponse {

    data class GetUser(val user: UserIF): UserResponse()
    data class GetInforUser(val user: com.example.myapplication.model.User): UserResponse()

    data class UpdateUserResponse(val message: String , val user:com.example.myapplication.model.User): UserResponse()
    data class GetIdUser(val userID: String): UserResponse()
    data class SaveFcmToken(val message: String): UserResponse()
    data class GetUserByName(val id: String,val fullname: String, val avatar: String): UserResponse()
    data class UserIF(
        val id: String,
        val email: String,
        val fullname: String,
        val avatar: String
    )
    data class FindUserResponse(
        val friends: List<User>,
        val notFriends: List<User>
    ): UserResponse()
    data class User(
        val id: String,
        val fullname: String,
        val avatar: String
    )

    data class ChangePasswordResponse(
        val message: String
    )
}
