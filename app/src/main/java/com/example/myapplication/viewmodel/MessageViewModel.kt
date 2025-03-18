package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositori.MessageResponse
import com.example.myapplication.services.RetrofitClient.messageService
import com.example.myapplication.utils.CheckExistChatRequest
import com.example.myapplication.utils.CreateChatRequest
import com.example.myapplication.utils.DeleteRequest
import com.example.myapplication.utils.SendMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageViewModel :ViewModel(){
    private val _createChatResponse = MutableLiveData<MessageResponse.CreateChat?>()
    private val _sendMessageResponse = MutableLiveData<MessageResponse.SendMessage?>()
    private val _getMessageResponse = MutableLiveData<MessageResponse.GetMessage?>()
    private val _getListChatResponse = MutableLiveData<MessageResponse.GetListChat?>()
    private val _checkExistChatResponse = MutableLiveData<MessageResponse.CheckExistChat?>()
    private val _deleteChatResponse = MutableLiveData<MessageResponse.DeleteChat?>()

    val createChatResponse: LiveData<MessageResponse.CreateChat?> get()= _createChatResponse
    val sendMessageResponse: LiveData<MessageResponse.SendMessage?> get()= _sendMessageResponse
    val getMessageResponse: LiveData<MessageResponse.GetMessage?> get()= _getMessageResponse
    val getListChatResponse: LiveData<MessageResponse.GetListChat?> get()= _getListChatResponse
    val checkExistChatResponse: LiveData<MessageResponse.CheckExistChat?> get()= _checkExistChatResponse
    val deleteChatResponse: LiveData<MessageResponse.DeleteChat?> get()= _deleteChatResponse

    fun checkExistChat(receiverId:String, senderId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    messageService.checkExistChat(CheckExistChatRequest(receiverId,senderId))
                }
                if (res.isSuccessful){
                    _checkExistChatResponse.postValue(res.body())
                }else{
                    Log.e("MessageViewModel", "Error: ${res.errorBody()?.string()}")
                    _checkExistChatResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("MessageViewModel", "checkExistChat failed: ${e.message}")
                _checkExistChatResponse.postValue(null)
            }
        }
    }

    fun getListChat(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listChatDeferred = async { messageService.getListChat(userId) }
                val listChat = listChatDeferred.await()

                withContext(Dispatchers.Main) {
                    _getListChatResponse.value = listChat.body()?.takeIf { listChat.isSuccessful }
                }

            }catch (e:Exception){
                Log.e("MessageViewModel", "Lỗi API: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    _getListChatResponse.value = null
                }
            }
        }
    }
    fun createChat(receiverId:String, senderId: String, content:String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    messageService.createChat(CreateChatRequest(receiverId,senderId,content))
                }
                if (res.isSuccessful){
                    _createChatResponse.postValue(res.body())
                }else{
                    Log.e("createChatViewModel", "Error: ${res.errorBody()?.string()}")
                    _createChatResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("createChatViewModel", "Message failed: ${e.message}")
                _createChatResponse.postValue(null)
            }
        }
    }

    fun sendMessage(chatId:String, senderId: String, content:String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    messageService.sendMessage(SendMessageRequest(chatId,senderId,content))
                }
                if (res.isSuccessful){
                    _sendMessageResponse.postValue(res.body())
                }else{
                    Log.e("sendMessageViewModel", "Error: ${res.errorBody()?.string()}")
                    _sendMessageResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("sendMessageViewModel", "Message failed: ${e.message}")
                _sendMessageResponse.postValue(null)
            }
        }
    }

    fun getMessage(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res = messageService.getMessage(chatId)
                if (res.isSuccessful) {
                    res.body()?.let {
                        _getMessageResponse.postValue(it)
                    } ?: run {
                        Log.e("sendMessageViewModel", "Response body is null")
                        _getMessageResponse.postValue(null)
                    }
                } else {
                    val errorMsg = res.errorBody()?.string() ?: "Unknown error"
                    Log.e("sendMessageViewModel", "API error: $errorMsg")
                    _getMessageResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("sendMessageViewModel", "Message failed: ${e.localizedMessage}")
                _getMessageResponse.postValue(null)
            }
        }
    }

    suspend fun deleteChat(chatId: String): Boolean {
        return try {
            val res = withContext(Dispatchers.IO) {
                messageService.deleteChat(DeleteRequest(chatId))
            }
            if (res.isSuccessful) {
                _deleteChatResponse.postValue(res.body())
                true
            } else {
                Log.e("MessageViewModel", "API error: ${res.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("MessageViewModel", "Delete failed: ${e.localizedMessage}")
            false
        }
    }


}