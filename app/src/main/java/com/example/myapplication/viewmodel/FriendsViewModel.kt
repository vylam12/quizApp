package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositori.FriendInvitationResponse
import com.example.myapplication.services.RetrofitClient.friendInvitationService
import com.example.myapplication.utils.AcceptFriendInvitedRequest
import com.example.myapplication.utils.FriendInvitedRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsViewModel : ViewModel(){
    private val _friendInvitedResponse = MutableLiveData<FriendInvitationResponse.FriendInvited?>()
    private val _acceptFriendInvitedResponse = MutableLiveData<FriendInvitationResponse.AcceptFriendInvited?>()
    private val _getFriendResponse = MutableLiveData<FriendInvitationResponse.GetFriend?>()
    private val _getFriendInvitedResponse = MutableLiveData<FriendInvitationResponse.GetFriendInvited?>()

    val friendInvitedResponse: LiveData<FriendInvitationResponse.FriendInvited?> get()= _friendInvitedResponse
    val acceptFriendInvitedResponse: LiveData<FriendInvitationResponse.AcceptFriendInvited?> get()= _acceptFriendInvitedResponse
    val getFriendResponse: LiveData<FriendInvitationResponse.GetFriend?> get()= _getFriendResponse
    val getFriendInvitedResponse: LiveData<FriendInvitationResponse.GetFriendInvited?> get()= _getFriendInvitedResponse

    fun loadAllFriend(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val friendsDeferred = async { friendInvitationService.getFriend(userId) }
                val invitedDeferred = async { friendInvitationService.getFriendInvited(userId) }

                val friends = friendsDeferred.await()
                val invited = invitedDeferred.await()

                withContext(Dispatchers.Main) {
                    _getFriendResponse.value = friends.body()
                    _getFriendInvitedResponse.value = invited.body()
//                    _getFriendResponse.value = if (friends.isSuccessful) friends.body() else null
//                    _getFriendInvitedResponse.value = if (invited.isSuccessful) invited.body() else null
                }
            }catch (e:Exception){
                Log.e("FriendsViewModel", "Lỗi API: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    _getFriendResponse.value = null
                    _getFriendInvitedResponse.value = null
                }
            }
        }
    }

    fun friendInvited(  idSender:String, idReciver: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    friendInvitationService.friendInvited(FriendInvitedRequest(idSender,idReciver))
                }
                if (res.isSuccessful){
                    _friendInvitedResponse.postValue(res.body())
                }else{
                    Log.e("FriendsViewModel", "Error: ${res.errorBody()?.string()}")
                    _friendInvitedResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("FriendsViewModel", "friendInvited failed: ${e.message}")
                _friendInvitedResponse.postValue(null)
            }
        }
    }

    fun acceptFriendInvited(  idSender:String, idReciver: String,status:String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    friendInvitationService.acceptFriendInvited(AcceptFriendInvitedRequest(idSender,idReciver,status))
                }
                if (res.isSuccessful){
                    _acceptFriendInvitedResponse.postValue(res.body())
                }else{
                    Log.e("FriendsViewModel", "Error: ${res.errorBody()?.string()}")
                    _acceptFriendInvitedResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("FriendsViewModel", "acceptFriendInvited failed: ${e.message}")
                _acceptFriendInvitedResponse.postValue(null)
            }
        }
    }

}