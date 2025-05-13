package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.repositori.MessageResponse
import com.example.myapplication.services.RetrofitClient.messageService
import com.example.myapplication.utils.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*


class MessageViewModel : ViewModel(){
    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> get() = _chatList

    private val _SearchChatResponse = MutableLiveData<MessageResponse.SearchChat?>()
    val searchChatResponse: LiveData<MessageResponse.SearchChat?> get()= _SearchChatResponse


    private val _filteredChatList = MutableLiveData<List<Chat>>()
    val filteredChatList: LiveData<List<Chat>> = _filteredChatList

    private val _createChatResponse = MutableLiveData<MessageResponse.CreateChat?>()
    private val _sendMessageResponse = MutableLiveData<MessageResponse.SendMessage?>()

    private val _checkExistChatResponse = MutableLiveData<MessageResponse.CheckExistChat?>()

    val createChatResponse: LiveData<MessageResponse.CreateChat?> get()= _createChatResponse
    val sendMessageResponse: LiveData<MessageResponse.SendMessage?> get()= _sendMessageResponse


    val checkExistChatResponse: LiveData<MessageResponse.CheckExistChat?> get()= _checkExistChatResponse

    private val db = FirebaseFirestore.getInstance()
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages
    private var messageListener: ListenerRegistration? = null
    private var lastVisibleMessage: DocumentSnapshot? = null
    private val seenMessageIds = mutableSetOf<String>()

    fun loadInitialMessages(chatId: String,currentUserId: String) {
        val messagesRef = db.collection("chat").document(chatId).collection("messages")

        messagesRef.orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val messages = snapshot.documents
                    .mapNotNull { it.toObject(Message::class.java) }
                    .reversed()

                _messages.postValue(messages.toMutableList())

                if (snapshot.size() > 0) {
                    lastVisibleMessage = snapshot.documents.last()
                }

                startListeningNewMessages(chatId,currentUserId)
            }
    }


    // LẮNG NGHE TIN NHẮN MƠÍ
    fun startListeningNewMessages(chatId: String, currentUserId: String) {
        val messagesRef = db.collection("chat").document(chatId).collection("messages")

        messageListener = messagesRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .whereGreaterThan("timestamp", Timestamp.now())
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    Log.e("MessageViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val newMessages = mutableListOf<Message>()
                for (change in snapshots.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        val doc = change.document
                        val message = doc.toObject(Message::class.java)

                        newMessages.add(message)

                        if (message.senderId != currentUserId && message.isRead == false) {
                            doc.reference.update("isRead", true)
                            Log.d("MessageViewModel", "Đánh dấu isRead cho tin mới: ${doc.id}")
                        }
                    }
                }

                if (newMessages.isNotEmpty()) {
                    val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                    currentMessages.addAll(newMessages)
                    _messages.postValue(currentMessages)
                }
            }
    }
    //LOAD THÊM 20 TIN NHẮN KHI KÉO LÊN XEM CUỘC TRÒ CHUYỆN
    fun loadMoreMessages(chatId: String) {
        val messagesRef = db.collection("chat").document(chatId).collection("messages")

        lastVisibleMessage?.let { lastDoc ->
            messagesRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastDoc)
                .limit(20)
                .get()
                .addOnSuccessListener { snapshot ->
                    val moreMessages = snapshot.documents
                        .mapNotNull { it.toObject(Message::class.java) }
                        .reversed()

                    val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                    currentMessages.addAll(0, moreMessages)
                    _messages.postValue(currentMessages)

                    if (snapshot.size() > 0) {
                        lastVisibleMessage = snapshot.documents.last()
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageListener?.remove()
    }

    // LẮNG NGHE CÁC ĐOẠN CHAT CỦA USER
    fun listenForChatUpdates(userId: String) {

        db.collection("chat")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("MessageViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    _chatList.postValue(emptyList())
                    return@addSnapshotListener
                }

                val userIdsToFetch = mutableSetOf<String>()
                val chatList = mutableListOf<Chat>()

                val documents = snapshots.documents
                var completedChats = 0

                for (document in documents) {
                    val chatId = document.id
                    val participants = (document.get("participants") as? List<String>)?.filter { it != userId }
                        ?: emptyList()
                    userIdsToFetch.addAll(participants)

                    val messagesRef = db.collection("chat").document(chatId)
                        .collection("messages")
                    messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { messageSnapshot, messageError ->
                            if (messageError != null) {
                                Log.e("MessageViewModel", "Error fetching messages", messageError)
                                return@addSnapshotListener
                            }

                            Log.d("MessageViewModel", "Tin nhắn đã được cập nhật:" +
                                    " ${messageSnapshot?.documents}")
                            if (messageSnapshot != null && !messageSnapshot.isEmpty) {
                                val messages = messageSnapshot.documents.mapNotNull { it.data }
                                val lastMsgData = messages.maxByOrNull { extractTimestamp(it) }
                                val lastMessage = lastMsgData?.get("translatedContent") as? String
                                val unreadCount = messages.count { msg ->
                                    val isUnread = msg["isRead"] as? Boolean == false
                                    val isFromOther = msg["senderId"] != userId
                                    isUnread && isFromOther
                                }

                                val userListRef = db.collection("users").whereIn("id",
                                    participants)
                                userListRef.get()
                                    .addOnSuccessListener { userSnapshot ->
                                        val usersInfo = userSnapshot.documents.map { doc ->
                                            Log.d("MessageViewModel","userListRef: ${doc}")
                                            UserChat(
                                                id = doc.id,
                                                fullname = doc.getString("fullname") ?: "",
                                                avatar = doc.getString("avatar") ?: ""
                                            )
                                        }

                                        val updatedChat = Chat(
                                            chatId = chatId,
                                            participants = participants,
                                            unreadCount = unreadCount,
                                            lastMessage = lastMessage,
                                            participantsInfo = usersInfo
                                        )
                                        val existingChatIndex = chatList.indexOfFirst { it.chatId == chatId }
                                        if (existingChatIndex != -1) {
                                            chatList[existingChatIndex] = updatedChat
                                        } else {
                                            chatList.add(updatedChat)
                                        }
                                        _chatList.postValue(chatList)
                                        _filteredChatList.postValue(chatList)
                                        completedChats++
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("MessageViewModel", "Error fetching user info", e)
                                    }
                            }
                        }
                }
            }
    }

    fun extractTimestamp(data: Map<String, Any>): Long {
        val ts = data["timestamp"]
        return when (ts) {
            is Timestamp -> ts.toDate().time
            is Long -> ts
            else -> 0L
        }
    }
    fun Chat.getReceiverInfo(currentUserId: String): UserChat? {
        val receiverId = participants.firstOrNull { it != currentUserId }
        return participantsInfo.firstOrNull { it.id == receiverId }
    }


    // lọc danh sách trò chuyện
    fun filterChatList(query: String, userId: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            _filteredChatList.postValue(_chatList.value)
            return
        }

        val currentChats = _chatList.value
        if (!currentChats.isNullOrEmpty()) {
            val filtered = currentChats.filter { chat ->
                chat.getReceiverInfo(userId)?.fullname
                    ?.contains(trimmedQuery, ignoreCase = true) == true
            }
            _filteredChatList.postValue(filtered)
            return
        }
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    messageService.searchChat(userId, trimmedQuery)
                }
                if (res.isSuccessful) {
                    _SearchChatResponse.postValue(res.body())
                } else {
                    Log.e("MessageViewModel", "Error: ${res.errorBody()?.string()}")
                    _SearchChatResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("MessageViewModel", "filterChatList failed: ${e.message}")
                _SearchChatResponse.postValue(null)
            }
        }
    }


    // KỂM TRA ĐOẠN CHAT GIỮA NGƯỜI NHẬN VÀ GỬI CÓ TỒN TẠI HAY KO
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
// tạo trò chuyện
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
// gửi tin nhắn
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

// đánh dâú tin nhan đã xem
    fun markMessagesAsSeen(chatId: String, currentUserId: String) {
        val messagesRef = db.collection("chat")
            .document(chatId)
            .collection("messages")

        messagesRef.get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    val senderId = document.getString("senderId")
                    val isRead = document.getBoolean("isRead") ?: false

                    // Chỉ cập nhật nếu sender != current user và chưa được đọc
                    if (senderId != currentUserId && !isRead) {
                        document.reference.update("isRead", true)
                            .addOnSuccessListener {
                                Log.d("ChatDebug", "Marked as read: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e("ChatDebug", "Update failed: $e")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatDebug", "Fetch failed: $e")
            }
    }

}