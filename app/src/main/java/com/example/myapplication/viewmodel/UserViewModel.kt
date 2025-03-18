package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositori.UserResponse
import com.example.myapplication.services.RetrofitClient.userService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel: ViewModel() {
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private val _getIdUserResponse = MutableLiveData<UserResponse.GetIdUser?>()
    val getIdUserResponse: LiveData<UserResponse.GetIdUser?> get()= _getIdUserResponse

    private val _getUserByNameResponse = MutableLiveData<List<UserResponse.GetUserByName>?>()
    val getUserByNameResponse: LiveData<List<UserResponse.GetUserByName>?> get()= _getUserByNameResponse
    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> get() = _networkError

    init {
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        if (firebaseUid != null) {
            getIdUserFromApi(firebaseUid)
        } else {
            _userId.value = ""
        }
    }
    fun getUserId(): String {
        return _userId.value ?: ""
    }

    fun getUserFCMToken(userId:String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task->
            if (task.isSuccessful){
                val token = task.result
                Log.d("FCM token", "User FCM Token: $token")
                sendTokenToServer(userId, token)
            }
        }
    }
    fun sendTokenToServer(userId: String, fcmToken:String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userService.saveFCMToken(userId, fcmToken)
                if (response.isSuccessful) {
                    Log.d("FCM", "Token sent successfully: ${response.body()}")
                } else {
                    Log.e("FCM", "Failed to send token: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error sending token", e)
            }
        }
    }

    private fun getIdUserFromApi(firebaseUid: String) {
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    userService.getIdUser(firebaseUid)
                }
                if (res.isSuccessful) {
                    val userIdFromApi = res.body()?.userID ?: ""
                    _userId.postValue(userIdFromApi)
                    _getIdUserResponse.postValue(res.body())
                    _networkError.postValue(false)
                } else {
                    Log.e("UserViewModel", "API Error: ${res.errorBody()?.string()}")
                    _userId.postValue("")
                    _getIdUserResponse.postValue(null)
                    _networkError.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "getIdUserFromApi failed: ${e.message}")
                _userId.postValue("")
                _getIdUserResponse.postValue(null)
                _networkError.postValue(true)
            }
        }
    }
    fun findUserIdByName(nameFriend: String, userId: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Searching user: $nameFriend for userId: $userId")
                val res = withContext(Dispatchers.IO) {
                    userService.findUserIdByName(nameFriend, userId)
                }
                if (res.isSuccessful) {
                    val userList = res.body() ?: emptyList()
                    Log.d("UserViewModel", "Found users: $userList")
                    _getUserByNameResponse.postValue(userList)  // Cập nhật LiveData
                } else {
                    _getUserByNameResponse.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "findUserIdByName failed: ${e.message}")
                _getUserByNameResponse.postValue(null)
            }
        }
    }


}