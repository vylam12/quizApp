package com.example.myapplication.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.repositori.UserResponse
import com.example.myapplication.services.RetrofitClient.userService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import java.io.File

class UserViewModel: ViewModel() {
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private val _getIdUserResponse = MutableLiveData<UserResponse.GetIdUser?>()
    val getIdUserResponse: LiveData<UserResponse.GetIdUser?> get()= _getIdUserResponse

    private val _getUserIF = MutableLiveData<UserResponse.GetUser?>()
    val getUserIFResponse: LiveData<UserResponse.GetUser?> get()= _getUserIF


    private val _getUserByNameResponse = MutableLiveData<List<UserResponse.GetUserByName>?>()
    val getUserByNameResponse: LiveData<List<UserResponse.GetUserByName>?> get()= _getUserByNameResponse

    private val _updateUserResponse = MutableLiveData<UserResponse.UpdateUserResponse?>()
    val updateUserResponse: LiveData<UserResponse.UpdateUserResponse?> get()= _updateUserResponse

    private val _friendsList = MutableLiveData<List<UserResponse.User>>()
    val friendsList: LiveData<List<UserResponse.User>> = _friendsList

    private val _notFriendsList = MutableLiveData<List<UserResponse.User>>()
    val notFriendsList: LiveData<List<UserResponse.User>> = _notFriendsList

    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> get() = _networkError

    fun getUserInformation(userId: String) {
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    userService.getUserInformation(userId)
                }
                if (res.isSuccessful) {
                    _getUserIF.postValue(res.body())
                } else {
                    _getUserIF.postValue(null)
                }
            } catch (e: Exception) {
                _getUserIF.postValue(null)
            }
        }
    }

    fun getUserFCMToken(userId:String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task->
            if (task.isSuccessful){
                val token = task.result
                Log.d("FCM token", "User FCM Token: $token")
                Log.d("FCM token", "User FCM Token: $token , UserID: ${userId}")
                sendTokenToServer(userId, token)
            }
        }
    }
    fun sendTokenToServer(userId: String, fcmToken:String){
        if (userId.isEmpty() || fcmToken.isEmpty()) {
            Log.e("FCM", "Dữ liệu không hợp lệ: userId=$userId, fcmToken=$fcmToken")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = mapOf("userId" to userId, "fcmToken" to fcmToken)
                Log.d("FCM", "Request body: $requestBody")
                val response = userService.saveFCMToken(requestBody)
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

    fun prepareImagePart(context: Context, uri: Uri?, partName: String = "avatar"): MultipartBody.Part? {
        if (uri == null) return null
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri) ?: "image/*"
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
        tempFile.outputStream().use { inputStream.copyTo(it) }
        val requestFile = tempFile.asRequestBody(type.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
    }

    fun updateUser(context: Context,userId:String, avatarUri: Uri?, gender: String?, dob: String?){
        viewModelScope.launch {
            try {
                val avatarPart = prepareImagePart(context, avatarUri)
                val genderBody = gender?.toRequestBody("text/plain".toMediaTypeOrNull())
                val dobBody = dob?.toRequestBody("text/plain".toMediaTypeOrNull())
                val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                val res = withContext(Dispatchers.IO) {
                    userService.updateUser(
                        userId = userIdBody,
                        avatar = avatarPart,
                        gender = genderBody,
                        dateOfBirth = dobBody
                    )
                }
                if (res.isSuccessful) {
                    _updateUserResponse.postValue(res.body())
                } else {
                    _updateUserResponse.postValue(null)

                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "update user failed: ${e.message}")
                _updateUserResponse.postValue(null)

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
                    _getUserByNameResponse.postValue(userList)
                } else {
                    _getUserByNameResponse.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "findUserIdByName failed: ${e.message}")
                _getUserByNameResponse.postValue(emptyList())
            }
        }
    }

    fun findUser(nameFriend: String, userId: String) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Searching user: $nameFriend for userId: $userId")
                val res = withContext(Dispatchers.IO) {
                    userService.findUser(nameFriend, userId)
                }
                if (res.isSuccessful) {
                    val responseBody = res.body()
                    if (responseBody != null) {
                        Log.d("UserViewModel", "Friends: ${responseBody.friends}, Not Friends: ${responseBody.notFriends}")
                        _friendsList.postValue(responseBody.friends)
                        _notFriendsList.postValue(responseBody.notFriends)
                    } else {
                        _friendsList.postValue(emptyList())
                        _notFriendsList.postValue(emptyList())
                    }
                } else {
                    _friendsList.postValue(emptyList())
                    _notFriendsList.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "findUser failed: ${e.message}")
                _friendsList.postValue(emptyList())
                _notFriendsList.postValue(emptyList())
            }
        }
    }



}