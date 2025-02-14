package com.example.myapplication.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.backend.model.Messages
import com.example.myapplication.databinding.ActivityChatBinding


class ChatActivity :AppCompatActivity(){
    private val messages = mutableListOf<Messages>()
    private lateinit var adapter: ChatAdapter
    private lateinit var  mBinding: ActivityChatBinding
    private val id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mBinding= ActivityChatBinding.inflate(LayoutInflater.from(this))
        adapter = ChatAdapter(messages,id)
        mBinding.recyclerViewMessages.layoutManager  =LinearLayoutManager(this)
        mBinding.recyclerViewMessages.adapter = adapter
        mBinding.buttonSend.setOnClickListener{
            val messageText= mBinding.editTextMessage.text.toString()
            if(messageText.isNotBlank()){
                messages.add(
                    Messages(
                        senderId = id,
                        content = messageText,
                        isRead = true,
                    ))
                adapter.notifyItemInserted(messages.size - 1)
                mBinding.editTextMessage.text.clear()
                // Giả lập phản hồi từ người khác
                messages.add(
                    Messages(
                        senderId = -1,
                        content = "Bot: $messageText",
                        isRead = false
                    )
                )
                adapter.notifyItemInserted(messages.size - 1)
            }
        }

    }
}