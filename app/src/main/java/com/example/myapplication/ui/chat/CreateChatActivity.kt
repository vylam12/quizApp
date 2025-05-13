package com.example.myapplication.ui.chat

import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ChatActivityCreateChatBinding
import com.example.myapplication.model.Message
import com.example.myapplication.repositori.MessageResponse
import com.example.myapplication.repositori.UserResponse
import com.example.myapplication.ui.BaseActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.MessageViewModel
import com.example.myapplication.viewmodel.TranslateViewModel
import com.example.myapplication.viewmodel.UserViewModel

class CreateChatActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ChatActivityCreateChatBinding
    private val userViewModel: UserViewModel by viewModels()
    private val translateViewModel: TranslateViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()

    private lateinit var senderId: String
    private lateinit var senderIdMG: String
    private lateinit var receiverId: String
    private var chatId: String? = null
    private var avatar: String = ""
    private var isInitialLoad = true
    private var lastMessageList: List<Message> = emptyList()
    private var suggestedUsers: List<UserResponse.GetUserByName> = emptyList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ChatActivityCreateChatBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        initUserInfo()
        setupAdapter()
        setupRecyclerView()
        setupSearchReceiver()
        setupObservers()
        setupClickListeners()
    }

    private fun initUserInfo() {
        val userInfo = UserPreferences.getUserData(this)
        senderId = userInfo["id"].toString()
        senderIdMG=userInfo["user_id"].toString()
    }

    private fun setupAdapter() {
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
    }

    private fun setupRecyclerView() {
        recyclerView = mBinding.recyclerCreateChat
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
    }

    private fun setupSearchReceiver() {
        mBinding.fromReceiver.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()
                if (input.length >= 1) {
                    userViewModel.findUserIdByName(input, senderIdMG)
                }
            }
        })

        mBinding.fromReceiver.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = suggestedUsers[position]
            receiverId = selectedUser.id
            avatar = selectedUser.avatar
            mBinding.fromReceiver.setText(selectedUser.fullname)

            chatAdapter = ChatAdapter(emptyList(), translateViewModel, this, avatar, receiverId)
            recyclerView.adapter = chatAdapter

            messageViewModel.checkExistChat(receiverId, senderId)
        }
    }

    private fun setupObservers() {
        userViewModel.getUserByNameResponse.observe(this) { userList ->
            if (!userList.isNullOrEmpty()) {
                suggestedUsers = userList
                val adapter = UserDropdownAdapter(this, suggestedUsers)
                mBinding.fromReceiver.setAdapter(adapter)
                if (adapter.count > 0) {
                    mBinding.fromReceiver.post { mBinding.fromReceiver.showDropDown() }
                }
            }
        }

        messageViewModel.checkExistChatResponse.observe(this) { res ->
            if (res != null) {
                chatId = res.chatId
                showChatMessages(res.chatId)
                Log.d("DEBUG", "Chat exists with chatId: ${res.chatId}")
            } else {
                showUserInfo()
                Log.d("DEBUG", "No chat found. Proceed to create new chat.")
            }
        }
        messageViewModel.createChatResponse.observe(this, object :
            Observer<MessageResponse.CreateChat?> {
            override fun onChanged(res: MessageResponse.CreateChat?) {
                if (res != null) {
                    chatId = res.chatId
                    showChatMessages(chatId!!)
                    messageViewModel.createChatResponse.removeObserver(this)
                }
            }
        })

        messageViewModel.messages.observe(this) { messages ->
            messages?.let {
                if (it != lastMessageList) {
                    lastMessageList = it
                    val layoutManager = mBinding.recyclerCreateChat.layoutManager as LinearLayoutManager
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                    val firstVisibleView = layoutManager.findViewByPosition(firstVisibleItem)
                    val topOffset = firstVisibleView?.top ?: 0
                    val oldItemCount = chatAdapter.itemCount
                    Log.d("DEBUG", "Received ${messages.size} messages from server")
                    chatAdapter.updateData(it)
                    if (isInitialLoad) {
                        recyclerView.scrollToPosition(it.size - 1)
                        isInitialLoad = false
                    } else if (firstVisibleItem == 0) {
                        val addedItems = chatAdapter.itemCount - oldItemCount
                        layoutManager.scrollToPositionWithOffset(addedItems, topOffset)
                    }
                }

            }
        }
    }

    private fun setupClickListeners() {
        mBinding.buttonSend.setOnClickListener(this)
    }

    private fun handleSendMessage() {
        val content = mBinding.editTextMessage.text.toString().trim()
        if (content.isEmpty()) return

        if (chatId != null) {
            messageViewModel.sendMessage(chatId!!, senderId, content)
//            recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            mBinding.editTextMessage.text.clear()
        } else {
            messageViewModel.createChat(receiverId, senderId, content)
            mBinding.editTextMessage.text.clear()
        }
    }

    private fun showChatMessages(chatId: String) {
        recyclerView.visibility = View.VISIBLE
        mBinding.userInfoLayout.visibility = View.GONE
        messageViewModel.startListeningNewMessages(chatId,senderId)
    }

    private fun showUserInfo() {
        recyclerView.visibility = View.GONE
        mBinding.userInfoLayout.visibility = View.VISIBLE
        mBinding.userName.text = mBinding.fromReceiver.text.toString()
        mBinding.userStatus.text = "No conversation yet"

        Glide.with(this)
            .load(avatar)
            .placeholder(R.drawable.useravatar)
            .error(R.drawable.useravatar)
            .into(mBinding.avatarReceiver)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> handleSendMessage()
            else -> Log.d("DEBUG", "Unknown view clicked")
        }
    }
}

