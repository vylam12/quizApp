package com.example.myapplication.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ChatActivityChatBinding
import com.example.myapplication.ui.BaseActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.MessageViewModel
import com.example.myapplication.viewmodel.TranslateViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.*

class ChatActivity :BaseActivity() ,View.OnClickListener, View.OnFocusChangeListener {
    private lateinit var  mBinding: ChatActivityChatBinding

    private val messageViewModel: MessageViewModel by viewModels()
    private val translateViewModel: TranslateViewModel by viewModels()

    private lateinit var chatAdapter: ChatAdapter
    private var isInitialLoad = true

    private lateinit var  chatId:String
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var receiverAvatar: String

    private var messageListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ChatActivityChatBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        chatId = intent.getStringExtra("CHAT_ID") ?:""
        receiverId = intent.getStringExtra("ID_OTHER") ?:""
        receiverName = intent.getStringExtra("FULLNAME") ?: "Unknown"
        receiverAvatar = intent.getStringExtra("AVATAR") ?: "Unknown"

        initUserInfo()
        observeMessages()

        setupUI()
        setupRecyclerView()


        if (chatId.isNotEmpty()) {
            messageViewModel.loadInitialMessages(chatId,senderId)
        }
        messageViewModel.markMessagesAsSeen(chatId,senderId)
    }

    private fun initUserInfo() {
        val userInfo = UserPreferences.getUserData(this)
        senderId= userInfo["id"].toString()
    }

    private fun setupUI(){
        val nameUserMessages: TextView = findViewById(R.id.nameUserMessages)
        val avatarImageView: ShapeableImageView = findViewById(R.id.avatarUserChat)

        nameUserMessages.text = receiverName
        Glide.with(avatarImageView.context)
            .load(receiverAvatar)
            .error(R.drawable.useravatar)
            .placeholder(R.drawable.useravatar)
            .into(avatarImageView)

        mBinding.apply{
            editTextMessage.onFocusChangeListener= this@ChatActivity
            buttonSend.setOnClickListener{
                val content = mBinding.editTextMessage.text.toString().trim()
                if (content.isEmpty())
                    return@setOnClickListener
                messageViewModel.sendMessage(chatId,senderId ,content)
                mBinding.editTextMessage.text.clear()
            }
            backMessage.setOnClickListener(this@ChatActivity)

            editTextMessage.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handleSend()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setupRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        chatAdapter = ChatAdapter(emptyList(), translateViewModel, this,receiverAvatar,receiverId)
        recyclerView.adapter = chatAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findFirstVisibleItemPosition() == 0) {
                    messageViewModel.loadMoreMessages(chatId)
                }
            }
        })
    }

    private fun observeMessages(){
        messageViewModel.messages.observe(this, Observer { messages ->
            messages?.let {
                val layoutManager = mBinding.recyclerViewMessages.layoutManager
                        as LinearLayoutManager

                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val firstVisibleView = layoutManager.findViewByPosition(firstVisibleItem)
                val topOffset = firstVisibleView?.top ?: 0
                val oldItemCount = chatAdapter.itemCount

                chatAdapter.updateData(it)

                if (isInitialLoad) {
                    mBinding.recyclerViewMessages.scrollToPosition(it.size - 1)
                    isInitialLoad = false
                } else if (firstVisibleItem == 0) {
                    val newItemCount = chatAdapter.itemCount
                    val addedItems = newItemCount - oldItemCount
                    layoutManager.scrollToPositionWithOffset(addedItems, topOffset)
                }
            }
        })
    }

    private fun handleSend() {
        mBinding.editTextMessage.clearFocus()
        val content = mBinding.editTextMessage.text.toString().trim()
        if (content.isNotEmpty()){
            messageViewModel.sendMessage(chatId,senderId ,content)
            mBinding.editTextMessage.text.clear()
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mBinding.editTextMessage.windowToken, 0)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonSend -> handleSend()
            R.id.backMessage -> finish()
            else -> Log.d("DEBUG", "Unknown view clicked")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        messageListener?.remove()
    }
}