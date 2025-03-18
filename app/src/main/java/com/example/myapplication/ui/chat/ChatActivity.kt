package com.example.myapplication.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ChatActivityChatBinding
import com.example.myapplication.model.Messages
import com.example.myapplication.repositori.MessageResponse
import com.example.myapplication.viewmodel.MessageViewModel
import com.example.myapplication.viewmodel.TranslateViewModel
import com.example.myapplication.viewmodel.UserViewModel

class ChatActivity :AppCompatActivity() ,View.OnClickListener, View.OnFocusChangeListener {
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var  mBinding: ChatActivityChatBinding
    private val messageViewModel: MessageViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val translateViewModel: TranslateViewModel by viewModels()
    private lateinit var  chatId:String
    private lateinit var receiverName: String
    private lateinit var senderId: String
    private lateinit var receiverId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ChatActivityChatBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        chatId = intent.getStringExtra("chatId") ?:""
        receiverId = intent.getStringExtra("otherUser") ?:""
        receiverName = intent.getStringExtra("name") ?: "Unknown"

        val nameUserMessages: TextView = findViewById(R.id.nameUserMessages)
        nameUserMessages.text = receiverName

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        userViewModel.userId.observe(this) { userId ->
            if (userId.isNotEmpty()) {
                senderId = userId
                chatAdapter = ChatAdapter(emptyList(),senderId, translateViewModel,this)
                recyclerView.adapter = chatAdapter

                getMessage(chatId)
            }
        }

        messageViewModel.sendMessageResponse.observe(this) { res ->
            if (res is MessageResponse.SendMessage && res.message == "Tin nhắn đã được gửi thành công!") {
                val newMessage = Messages(
                    id = System.currentTimeMillis().toString(),
                    content = res.newMessage.content,
                    id_sender = senderId,
                    translatedContent= res.newMessage.translatedContent,
                    chatId = chatId,
                    isRead = false,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )
                chatAdapter.addMessage(newMessage)
                mBinding.recyclerViewMessages.scrollToPosition(chatAdapter.itemCount - 1)
                mBinding.editTextMessage.setText("")
            }
        }

        mBinding.apply{
            editTextMessage.onFocusChangeListener= this@ChatActivity
            buttonSend.setOnClickListener(this@ChatActivity)
        }
    }
    private fun handleSend() {
        val content = mBinding.editTextMessage.text.toString()
        if (content.isNotEmpty()){
            Log.d("Handle send",senderId)
            messageViewModel.sendMessage(chatId,senderId ,content)
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> handleSend()
            else -> Log.d("DEBUG", "Unknown view clicked")
        }
    }

    private fun getMessage(chatId: String){
        messageViewModel.getMessage(chatId)
        messageViewModel.getMessageResponse.observe(this){res->
            if (res!=null){
                val listMessage = res.mongoMessages
                chatAdapter.updateData(listMessage)
            }
        }
    }

}