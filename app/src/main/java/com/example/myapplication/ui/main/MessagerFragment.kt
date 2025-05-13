package com.example.myapplication.ui.main

import android.content.*
import android.net.*
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.myapplication.R
import com.example.myapplication.viewmodel.MessageViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.ui.chat.CreateChatActivity
import com.example.myapplication.utils.UserPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MessagerFragment : Fragment() {
    private val messageViewModel: MessageViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var searchAdapter: SearchMessageAdapter
    private lateinit var searchRecyclerView: RecyclerView

    private lateinit var recyclerView: RecyclerView
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var cardViewNoMessage: CardView
    private lateinit var cardViewNoInternet: CardView
    private lateinit var searchMessage: EditText
    private lateinit var userIdFB: String
    private lateinit var userIdMG: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messager, container, false)

        val userInfo = UserPreferences.getUserData(requireContext())
        recyclerView = view.findViewById(R.id.recyclerMessager)
        val btnNewMessage = view.findViewById<FloatingActionButton>(R.id.btn_new_message)
        lottieAnimationView = view.findViewById(R.id.loading_chat)
        searchRecyclerView = view.findViewById(R.id.recyclerSearchResults)
        searchAdapter = SearchMessageAdapter(emptyList())
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = searchAdapter

        cardViewNoMessage = view.findViewById(R.id.cardViewNoMessage)
        cardViewNoInternet = view.findViewById(R.id.cardViewNoInternet)
        searchMessage = view.findViewById(R.id.searchChat)
        userIdFB = userInfo["id"].toString()
        userIdMG = userInfo["user_id"].toString()
        setupRecyclerView()

        searchMessage.setOnTouchListener { _, _ ->
            searchMessage.isFocusableInTouchMode = true
            searchMessage.requestFocus()
            false
        }

        searchMessage.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            if (query.isNotEmpty()) {
                messageViewModel.filterChatList(query, userIdFB)
                messageViewModel.filteredChatList.observe(viewLifecycleOwner) { filteredChatList ->
                    searchAdapter.updateData(filteredChatList)
                    searchRecyclerView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } else {
                searchRecyclerView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                loadMessages()
            }
        }


        if (!isInternetAvailable()) {
            showNoInternet()
        } else {
            loadMessages()
        }

        btnNewMessage.setOnClickListener {
            val intent = Intent(requireContext(), CreateChatActivity::class.java)
            startActivity(intent)
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        messageAdapter.stopListening()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        messageAdapter = MessageAdapter(mutableListOf())
        recyclerView.adapter = messageAdapter
    }


    private fun loadMessages() {
        showLoading(true)
        messageViewModel.listenForChatUpdates(userIdFB)
        messageViewModel.chatList.observe(viewLifecycleOwner, Observer{ chatList ->
            messageAdapter.updateData(chatList)
            checkIfNoMessages()
            showLoading(false)

            recyclerView.adapter = messageAdapter
        })

    }
    private fun showLoading(isLoading: Boolean) {
        lottieAnimationView.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE  else View.VISIBLE
    }
    private fun showNoInternet() {
        cardViewNoInternet.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        cardViewNoMessage.visibility = View.GONE
    }
    private fun checkIfNoMessages() {
        if (messageAdapter.itemCount == 0) {
            cardViewNoMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            cardViewNoMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}