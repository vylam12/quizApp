package com.example.myapplication.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ChatActivityCreateChatBinding
import com.example.myapplication.repositori.UserResponse
import com.example.myapplication.viewmodel.MessageViewModel
import com.example.myapplication.viewmodel.TranslateViewModel
import com.example.myapplication.viewmodel.UserViewModel

class CreateChatActivity:AppCompatActivity(), View.OnClickListener{
    private lateinit var  mBinding: ChatActivityCreateChatBinding
    private val userViewModel: UserViewModel by viewModels()
    private val translateViewModel: TranslateViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()
    private lateinit var senderId: String
    private lateinit var receiverId:  String

    private lateinit var adapter: ArrayAdapter<String>
    private var suggestedUsers: List<UserResponse.GetUserByName> = emptyList()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding= ChatActivityCreateChatBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        mBinding.fromReceiver.setAdapter(adapter)
        Log.d("CreateChatActivity", "Adapter đã gán vào AutoCompleteTextView")

        recyclerView = mBinding.recyclerCreateChat
        recyclerView.layoutManager = LinearLayoutManager(this).apply{
            stackFromEnd = true
        }

        userViewModel.userId.observe(this) { userId ->
            Log.d("CreateChatActivity", "userId received: $userId")
            if (userId.isNotEmpty()) {
                senderId = userId
                Log.d("CreateChatActivity", "senderId assigned: $senderId")
            }
        }

        mBinding.fromReceiver.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()
                Log.d("CreateChatActivity", "User input: $input")
                if (input.length >= 1 && senderId.isNotEmpty()){
                    Log.d("CreateChatActivity", "Calling API: findUserIdByName")
                    userViewModel.findUserIdByName(input, senderId)
                }
            }
            override fun afterTextChanged(s: Editable?) {}

        })

        userViewModel.getUserByNameResponse.observe(this){userList->
            if (!userList.isNullOrEmpty()){
                suggestedUsers = userList
                val names = userList.map { it.fullname }

                adapter.clear()
                adapter.addAll(names)
                adapter.notifyDataSetChanged()
                mBinding.fromReceiver.post {
                    mBinding.fromReceiver.requestFocus() // Đảm bảo focus
                    Log.d("CreateChatActivity", "Gọi showDropDown()")
                    mBinding.fromReceiver.showDropDown()
                }

            }
        }
        mBinding.fromReceiver.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = suggestedUsers[position] // Lấy user trực tiếp từ danh sách

            receiverId = selectedUser.id
            mBinding.fromReceiver.setText(selectedUser.fullname)
            chatAdapter = ChatAdapter(emptyList(), senderId,translateViewModel, this)
            recyclerView.adapter = chatAdapter

            messageViewModel.checkExistChat(receiverId,senderId)

        }
        messageViewModel.checkExistChatResponse.observe(this){ res->
            if (res!= null) {
                showChatMessages(res.chatId)
            }else{
                showUserInfo()
            }
        }

        mBinding.apply {
            buttonSend.setOnClickListener(this@CreateChatActivity)
        }
    }

    private fun showChatMessages(chatId: String) {
        recyclerView.visibility = View.VISIBLE
        mBinding.userInfoLayout.visibility = View.GONE

        messageViewModel.getMessage(chatId)
        messageViewModel.getMessageResponse.observe(this) { res ->
            if (res!=null){
                val listMessage = res?.mongoMessages ?: emptyList()
                chatAdapter.updateData(listMessage)
            }
        }
    }
    private fun showUserInfo() {
        recyclerView.visibility = View.GONE
        mBinding.userInfoLayout.visibility = View.VISIBLE

        mBinding.userName.text = mBinding.fromReceiver.text.toString()
        mBinding.userStatus.text = "Chưa có cuộc trò chuyện"
    }

    private fun handleSenMessage() {
        val content = mBinding.editTextMessage.text.toString()

        mBinding.fromReceiver.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                receiverId = mBinding.fromReceiver.text.toString().trim()
            }
        }
        if (content.isNotEmpty()){
            messageViewModel.createChat(receiverId, senderId, content)
            mBinding.editTextMessage.text.clear()
        }
        messageViewModel.createChatResponse.observe(this){chatId->
            var chatId: String? = null
            if (!chatId.isNullOrEmpty()) {
                messageViewModel.getMessage(chatId)
            }
        }

        messageViewModel.getMessageResponse.observe(this) { res ->
            if (res != null) {
                chatAdapter.updateData(res.mongoMessages)
                recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> handleSenMessage()
            else -> Log.d("DEBUG", "Unknown view clicked")
        }
    }





}