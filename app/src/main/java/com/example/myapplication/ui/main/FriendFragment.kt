package com.example.myapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.R
import com.example.myapplication.ui.profile.ProfileActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.FriendsViewModel
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayout

class FriendFragment : Fragment() {
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var recyclerFriend: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var rcSearch: RecyclerView
    private lateinit var searchFriend: EditText
    private lateinit var viewNoFriends: View
    private lateinit var viewNoSuggestions: View
    private lateinit var lottieAnimationView: LottieAnimationView


    private lateinit var userIdMG: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        val userInfo = UserPreferences.getUserData(requireContext())
        userIdMG = userInfo["user_id"].toString()

        recyclerFriend = view.findViewById(R.id.recyclerFriend)
        rcSearch = view.findViewById(R.id.rcSearch)
        tabLayout = view.findViewById(R.id.tabLayoutFriend)
        searchFriend = view.findViewById(R.id.searchFriend)
        viewNoFriends = view.findViewById(R.id.viewNoFriends)
        viewNoSuggestions = view.findViewById(R.id.viewNoSuggestions)
        lottieAnimationView = view.findViewById(R.id.loading_friend)

        recyclerFriend.layoutManager = LinearLayoutManager(requireContext())
        recyclerFriend.setHasFixedSize(true)
        friendAdapter = FriendAdapter( friends =emptyList(), true,friendsViewModel,
            onFriendClick = { user ->
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                Log.d("FriendFragment","userId other ${user._id}")
                intent.putExtra("USER_ID", user._id)
                startActivity(intent)
            })
        recyclerFriend.adapter = friendAdapter

        rcSearch.layoutManager = LinearLayoutManager(requireContext())
        rcSearch.setHasFixedSize(true)

        setupSearch()
        val userId = UserPreferences.getUserId(requireContext())  ?: ""

        friendsViewModel.loadAllFriend(userId)

        searchAdapter = SearchAdapter(emptyList(),friendsViewModel, userId )
        rcSearch.adapter = searchAdapter


        lottieAnimationView.visibility = View.VISIBLE
        recyclerFriend.visibility = View.GONE

        friendsViewModel.getFriendResponse.observe(viewLifecycleOwner) {
            lottieAnimationView.visibility = View.GONE
            recyclerFriend.visibility = View.VISIBLE
            updateUI(recyclerFriend, viewNoFriends, viewNoSuggestions)
        }
        friendsViewModel.getFriendInvitedResponse.observe(viewLifecycleOwner) {
            updateUI(recyclerFriend, viewNoFriends, viewNoSuggestions)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateUI(recyclerFriend, viewNoFriends, viewNoSuggestions)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
        return view
    }

    private fun setupSearch(){
        searchFriend.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchUser(s.toString())
            }

        })
    }

    private fun searchUser(name:String){
        userViewModel.findUser(name, userIdMG)

        userViewModel.notFriendsList.observe(viewLifecycleOwner) { notFriends ->
            userViewModel.friendsList.observe(viewLifecycleOwner) { friends ->
            if (notFriends.isNotEmpty()|| friends.isNotEmpty()) {
                rcSearch.visibility = View.VISIBLE
                recyclerFriend.visibility = View.GONE
                tabLayout.visibility = View.GONE
                viewNoFriends.visibility = View.GONE

                val allSearchResults = notFriends + friends
                searchAdapter.updateData(allSearchResults, friends)
                rcSearch.adapter = searchAdapter
            } else {
                rcSearch.visibility = View.GONE
                recyclerFriend.visibility = View.VISIBLE
            }
            }
        }
    }

    private fun updateUI(recyclerView: RecyclerView, viewNoFriends: View, viewNoSuggestions: View) {
        when (tabLayout.selectedTabPosition) {
            0 -> {
                val friends = friendsViewModel.getFriendResponse.value?.friend ?: emptyList()

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