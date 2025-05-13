package com.example.myapplication.services

import com.example.myapplication.repositori.AuthResponse
import com.example.myapplication.utils.ForgotPasswordRequest
import com.example.myapplication.utils.LoginRequest
import com.example.myapplication.utils.RegisterRequest
import com.example.myapplication.utils.ResetPasswordRequest
import com.example.myapplication.utils.VerifyOTPRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse.Register>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse.Login>

    @POST("forgot-password-otp")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest)
    :Response<AuthResponse.ForgotPassword>

    @POST("verify-otp")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<AuthResponse.VerifyOTP>

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<AuthResponse.ResetPassword>


}