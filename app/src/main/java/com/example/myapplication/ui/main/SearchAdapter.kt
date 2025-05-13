package com.example.myapplication.ui.main

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.repositori.UserResponse
import com.example.myapplication.viewmodel.FriendsViewModel
import com.google.android.material.imageview.ShapeableImageView

class SearchAdapter(
    private var friends: List<UserResponse.User>,
    private val friendViewModel: FriendsViewModel,
    private val userId: String,
    private var currentFriends: List<UserResponse.User> = emptyList()
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(){
    fun updateData(newFriends: List<UserResponse.User>, currentFriends: List<UserResponse.User>) {
        this.friends = newFriends
        this.currentFriends = currentFriends
        notifyDataSetChanged()
    }
    class SearchViewHolder(view: View): RecyclerView.ViewHolder(view){
        val nameTextView : TextView = view.findViewById(R.id.nameFriendSearch)
        val avatar: ShapeableImageView = view.findViewById(R.id.avatarFriendSearch)
        val btnAdd: ImageButton = view.findViewById(R.id.addFriend)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_search_user, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val friend = friends[position]
        holder.nameTextView.text = friend.fullname
        Glide.with( holder.avatar.context)
            .load(friend.avatar)
            .error(R.drawable.useravatar)
            .placeholder(R.drawable.useravatar)
            .into(holder.avatar)

        val isFriend = currentFriends.any { it.id == friend.id}
        if (isFriend) {
            holder.btnAdd.setImageResource(R.drawable.ic_chat_bubble)
            holder.btnAdd.isEnabled = true
            holder.btnAdd.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Mở đoạn chat với ${friend.fullname}", Toast.LENGTH_SHORT).show()
            }
        } else {
            var isAdded = false
            holder.btnAdd.setOnClickListener {
                if (!isAdded) {
                    friendViewModel.friendInvited(idSender= userId, idReciver =  friend.id)
                    holder.btnAdd.setImageResource(R.drawable.ic_added_friend)
                    holder.btnAdd.isEnabled = false
                    isAdded = true
                }
            }
        }


    }

    override fun getItemCount(): Int = friends.size
}