package com.example.myapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositori.AuthResponse
import com.example.myapplication.services.RetrofitClient.authService
import com.example.myapplication.utils.ForgotPasswordRequest
import com.example.myapplication.utils.LoginRequest
import com.example.myapplication.utils.RegisterRequest
import com.example.myapplication.utils.ResetPasswordRequest
import com.example.myapplication.utils.VerifyOTPRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel:ViewModel() {
    private val _loginResponse = MutableLiveData<AuthResponse.Login?>()
    private val _registerResponse = MutableLiveData<AuthResponse.Register?>()
    private val _forgotPasswordResponse = MutableLiveData<AuthResponse.ForgotPassword?>()
    private val _verifyOTPResponse = MutableLiveData<AuthResponse.VerifyOTP?>()
    private val _resetPasswordResponse = MutableLiveData<AuthResponse.ResetPassword?>()

    val loginResponse: LiveData<AuthResponse.Login?> get()= _loginResponse
    val registerResponse: LiveData<AuthResponse.Register?> get()= _registerResponse
    val ForgotPasswordResponse: LiveData<AuthResponse.ForgotPassword?> get()= _forgotPasswordResponse
    val VerifyOTPResponse: LiveData<AuthResponse.VerifyOTP?> get()= _verifyOTPResponse
    val ResetPasswordResponse: LiveData<AuthResponse.ResetPassword?> get()= _resetPasswordResponse

    fun register(email:String, password: String, fullname:String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    authService.register(RegisterRequest(email,password,fullname))
                }
                if (res.isSuccessful){
                    _registerResponse.postValue(res.body())
                }else{
                    Log.e("RegisterViewModel", "Error: ${res.errorBody()?.string()}")
                    _registerResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("AuthViewModel", "Register failed: ${e.message}")
                _registerResponse.postValue(null)
            }
        }
    }

    fun login(context: Context, idToken:String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    authService.login(LoginRequest(idToken))
                }
                if (res.isSuccessful){
                    val loginData = res.body()
                    if (loginData!= null){
                        saveToken(context, loginData.token)
                        _loginResponse.postValue(loginData)
                    }else{
                        _loginResponse.postValue(null)
                    }
                }else{
                    Log.e("LoginViewModel", "Error: ${res.errorBody()?.string()}")
                    _loginResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("AuthViewModel", "Login failed: ${e.message}")
                _loginResponse.postValue(null)
            }
        }
    }

    fun forgotPassword(email:String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    authService.forgotPassword(ForgotPasswordRequest(email))
                }
                if (res.isSuccessful){
                    _forgotPasswordResponse.postValue(res.body())
                }else{
                    Log.e("ForgotViewModel", "Error: ${res.errorBody()?.string()}")
                    _forgotPasswordResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("AuthViewModel", "Forgot password failed: ${e.message}")
                _forgotPasswordResponse.postValue(null)
            }
        }
    }
    fun verifyOTP(email:String, otp: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    authService.verifyOTP(VerifyOTPRequest(email,otp))
                }
                if (res.isSuccessful){
                    _verifyOTPResponse.postValue(res.body())
                }else{
                    Log.e("VerifyOTPViewModel", "Error: ${res.errorBody()?.string()}")
                    _verifyOTPResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("AuthViewModel", "VerifyOTP failed: ${e.message}")
                _verifyOTPResponse.postValue(null)
            }
        }
    }

    fun resetPassword(token:String,newPassword: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    authService.resetPassword(ResetPasswordRequest(token,newPassword))
                }
                if (res.isSuccessful){
                    _resetPasswordResponse.postValue(res.body())
                }else{
                    Log.e("VerifyOTPViewModel", "Error: ${res.errorBody()?.string()}")
                    _resetPasswordResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("AuthViewModel", "VerifyOTP failed: ${e.message}")
                _resetPasswordResponse.postValue(null)
            }
        }
    }

    private fun saveToken(context: Context, token:String){
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE )
        sharedPreferences.edit().putString("JWT_TOKEN",token).apply()
    }

}