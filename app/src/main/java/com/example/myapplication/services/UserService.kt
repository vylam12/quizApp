package com.example.myapplication.services

import com.example.myapplication.repositori.UserResponse
import com.example.myapplication.utils.ChangePasswordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    // ko lay full infor
    @GET("users/{userId}")
    suspend fun getUserInformation(@Path("userId") userId: String): Response<UserResponse.GetUser>

    //lay full infor
    @GET("get-infor-user/{userId}")
    suspend fun getInforUser(@Path("userId") userId: String): Response<UserResponse.GetInforUser>



    @GET("get-idUser/{userId}")
    suspend fun getIdUser(@Path("userId") userId: String): Response<UserResponse.GetIdUser>

    @GET("find-friend")
    suspend fun findUserIdByName(
        @Query("nameFriend") nameFriend: String,
        @Query("userId") userId: String
        ): Response<List<UserResponse.GetUserByName>>

    @GET("find-user")
    suspend fun findUser(
        @Query("nameFriend") nameFriend: String,
        @Query("userId") userId: String
    ): Response<UserResponse.FindUserResponse>

    @POST("save-fcm-token")
    suspend fun saveFCMToken(@Body body: Map<String, String>): Response<UserResponse.SaveFcmToken>
    @Multipart
    @POST("update-user")
    suspend fun updateUser(
        @Part("userId") userId: RequestBody,
        @Part avatar: MultipartBody.Part?,
        @Part("gender") gender: RequestBody?,
        @Part("birthDay") dateOfBirth: RequestBody?
    ): Response<UserResponse.UpdateUserResponse>

    @POST("change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<UserResponse.ChangePasswordResponse>
}