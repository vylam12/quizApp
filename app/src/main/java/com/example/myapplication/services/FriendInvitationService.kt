package com.example.myapplication.services

import com.example.myapplication.repositori.FriendInvitationResponse
import com.example.myapplication.utils.AcceptFriendInvitedRequest
import com.example.myapplication.utils.FriendInvitedRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendInvitationService {
    @POST("friend-invited")
    suspend fun friendInvited(@Body request: FriendInvitedRequest): Response<FriendInvitationResponse.FriendInvited>

    @POST("accept-friend-invited")
    suspend fun acceptFriendInvited(@Body request: AcceptFriendInvitedRequest): Response<FriendInvitationResponse.AcceptFriendInvited>

    @GET("get-friend/{userId}")
    suspend fun getFriend(@Path("userId") userId: String): Response<FriendInvitationResponse.GetFriend>

    @GET("get-friend-invited/{userId}")
    suspend fun getFriendInvited(@Path("userId") userId: String): Response<FriendInvitationResponse.GetFriendInvited>

}