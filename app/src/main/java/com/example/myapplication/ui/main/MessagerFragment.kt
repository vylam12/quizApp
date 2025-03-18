package com.example.myapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.viewmodel.MessageViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.ui.chat.CreateChatActivity
import com.example.myapplication.utils.SwipeToDeleteCallback
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MessagerFragment : Fragment() {
    private val messageViewModel: MessageViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messager, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMessager)
        val btnNewMessage = view.findViewById<FloatingActionButton>(R.id.btn_new_message)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val cardViewNoMessage = view.findViewById<CardView>(R.id.cardViewNoMessage)
        val cardViewNoInternet = view.findViewById<CardView>(R.id.cardViewNoInternet)

        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        messageAdapter = MessageAdapter(mutableListOf(), "")
        recyclerView.adapter = messageAdapter

        fun showLoading(isLoading: Boolean) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(messageAdapter, requireContext()){position ->
            val chatId = messageAdapter.getChatId(position)

            viewLifecycleOwner.lifecycleScope.launch {
                val result = messageViewModel.deleteChat(chatId) // Bây giờ trả về Boolean
                if (result) {
                    Toast.makeText(requireContext(), "Xóa cuộc trò chuyện thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi xóa cuộc trò chuyện", Toast.LENGTH_SHORT).show()
                }
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        userViewModel.userId.distinctUntilChanged().observe(viewLifecycleOwner) { userId ->
            if (userId.isNotEmpty()) {
                Log.e("<MessageFragment", "User ID: $userId")
                messageAdapter = MessageAdapter(mutableListOf(), userId = userId)
                recyclerView.adapter = messageAdapter

                showLoading(true)
                messageViewModel.getListChat(userId)
            }else {
                cardViewNoInternet.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                cardViewNoMessage.visibility = View.GONE
            }
        }

        messageViewModel.getListChatResponse.observe(viewLifecycleOwner) {res ->
            val listChat = res?.listChat ?: emptyList()
            Log.d("MessagerFragment", "List chat size: ${listChat.size}")

            showLoading(false)
            if (listChat.isEmpty()) {
                cardViewNoMessage.visibility =View.VISIBLE
            } else {
                cardViewNoMessage.visibility =View.GONE
                messageAdapter.updateMessages(listChat)
            }
        }

        btnNewMessage.setOnClickListener {
            val intent = Intent(requireContext(), CreateChatActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}