package com.example.myapplication.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Chat
import com.google.android.material.imageview.ShapeableImageView

class SearchMessageAdapter(private var chatList: List<Chat>) :
    RecyclerView.Adapter<SearchMessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.fullnameMessager)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.lastMessages)
        val unreadTextView: TextView = itemView.findViewById(R.id.unSeenMessages)
        val profileImageView: ShapeableImageView = itemView.findViewById(R.id.chatAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_messager, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        val userName = chat.participantsInfo.firstOrNull()?.fullname ?: "No Name"
        holder.txtName.text = userName
        if(chat.unreadCount == 0){
            holder.unreadTextView.visibility = View.GONE
        } else{
            holder.unreadTextView.visibility = View.VISIBLE
            holder.unreadTextView.text = chat.unreadCount.toString()
        }

        Glide.with(holder.profileImageView.context)
            .load(chat.participantsInfo.firstOrNull()?.avatar)
            .error(R.drawable.useravatar)
            .placeholder(R.drawable.useravatar)
            .into(holder.profileImageView)

        holder.lastMessageTextView.text = chat.lastMessage ?: "No messages yet"

    }

    fun updateData(newList: List<Chat>) {
        chatList = newList
        notifyDataSetChanged()
    }
}
