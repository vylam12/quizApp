package com.example.myapplication.services

import com.example.myapplication.repositori.MessageResponse
import com.example.myapplication.utils.CheckExistChatRequest
import com.example.myapplication.utils.CreateChatRequest
import com.example.myapplication.utils.DeleteRequest
import com.example.myapplication.utils.SendMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageService {
    @POST("create-chat")
    suspend fun createChat(@Body request: CreateChatRequest): Response<MessageResponse.CreateChat>

    @POST("send-message")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<MessageResponse.SendMessage>

    @GET("get-message/{chatId}")
    suspend fun getMessage(@Path("chatId") chatId: String): Response<MessageResponse.GetMessage>

    @GET("get-list-chat/{userId}")
    suspend fun getListChat(@Path("userId") userId: String): Response<MessageResponse.GetListChat>

    @POST("check-exist-chat")
    suspend fun checkExistChat(@Body request: CheckExistChatRequest): Response<MessageResponse.CheckExistChat>

    @POST("delete-chat")
    suspend fun deleteChat(@Body request: DeleteRequest): Response<MessageResponse.DeleteChat>


}