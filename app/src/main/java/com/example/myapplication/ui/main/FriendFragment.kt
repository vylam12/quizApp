package com.example.myapplication.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.viewmodel.FriendsViewModel
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayout

class FriendFragment : Fragment() {
    private val friendsViewModel: FriendsViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFriend)
        val viewNoFriends = view.findViewById<View>(R.id.viewNoFriends)
        tabLayout = view.findViewById(R.id.tabLayoutFriend)
        val viewNoSuggestions = view.findViewById<View>(R.id.viewNoSuggestions)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        friendAdapter = FriendAdapter(emptyList(), true,friendsViewModel ) // Khởi tạo adapter một lần duy nhất
        recyclerView.adapter = friendAdapter
        Log.d("FriendFragment", "RecyclerView visibility: ${recyclerView.visibility}")
        Log.d("FriendFragment", "Adapter item count: ${friendAdapter.itemCount}")
        userViewModel.userId.observe(viewLifecycleOwner) { userId ->
            if (userId.isNotEmpty()) {
                Log.e("FriendFragment", "User ID: $userId")
                friendsViewModel.loadAllFriend(userId)
            }
        }

        friendsViewModel.getFriendResponse.observe(viewLifecycleOwner) {
            Log.d("FriendFragment", "getFriendResponse cập nhật: ${it?.friend}")
            updateUI(recyclerView, viewNoFriends, viewNoSuggestions)
        }
        friendsViewModel.getFriendInvitedResponse.observe(viewLifecycleOwner) {
            Log.d("FriendFragment", "getFriendInvitedResponse cập nhật: ${it?.friend}")
            updateUI(recyclerView, viewNoFriends, viewNoSuggestions)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateUI(recyclerView, viewNoFriends, viewNoSuggestions)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
        return view
    }

    private fun updateUI(recyclerView: RecyclerView, viewNoFriends: View, viewNoSuggestions: View) {
        when (tabLayout.selectedTabPosition) {
            0 -> {
                val friends = friendsViewModel.getFriendResponse.value?.friend ?: emptyList()
                Log.d("FriendFragment", "Danh sách bạn bè $friends")
                if (friends.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    viewNoSuggestions.visibility = View.GONE
                    viewNoFriends.visibility = View.GONE
                    friendAdapter.updateData(friends, true)
                } else {
                    recyclerView.visibility = View.GONE
                    viewNoFriends.visibility = View.VISIBLE
                    viewNoSuggestions.visibility = View.GONE
                }
            }
            1 -> {
                val invitedFriends = friendsViewModel.getFriendInvitedResponse.value?.friend ?: emptyList()
                if (invitedFriends.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    viewNoFriends.visibility = View.GONE
                    viewNoSuggestions.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    viewNoSuggestions.visibility = View.GONE
                    viewNoFriends.visibility = View.GONE
                    friendAdapter.updateData(invitedFriends, false)
                }
            }
        }
    }
}