package com.example.myapplication.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL="https://server-chat-app-y3i0.onrender.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy{ retrofit.create(AuthService::class.java)}
    val translateService: TranslateService by  lazy{ retrofit.create(TranslateService::class.java)}
    val messageService: MessageService by  lazy{ retrofit.create(MessageService::class.java)}
    val friendInvitationService: FriendInvitationService by lazy{ retrofit.create(FriendInvitationService::class.java)}
    val userService: UserService by lazy{ retrofit.create(UserService::class.java)}
    val quizService: QuizService by lazy{ retrofit.create(QuizService::class.java)}
}