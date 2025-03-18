package com.example.myapplication.services

import com.example.myapplication.repositori.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @GET("get-idUser/{userId}")
    suspend fun getIdUser(@Path("userId") userId: String): Response<UserResponse.GetIdUser>

    @GET("find-friend")
    suspend fun findUserIdByName(
        @Query("nameFriend") nameFriend: String,
        @Query("userId") userId: String
        ): Response<List<UserResponse.GetUserByName>>

    @POST("save-fcm-token")
    suspend fun saveFCMToken(
        @Query("userId") userId: String,
        @Query("fcmToken") fcmToken: String
    ): Response<UserResponse.SaveFcmToken>
}