package com.example.myapplication.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.ListChat
import com.example.myapplication.ui.chat.ChatActivity

class MessageAdapter(
    private var messages: MutableList<ListChat>,
    private val userId:String
) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){
        class MessageViewHolder(view:View): RecyclerView.ViewHolder(view){
            val nameTextView: TextView = view.findViewById(R.id.fullnameMessager)
            val lastMessageTextView: TextView = view.findViewById(R.id.lastMessages)
            val unreadTextView: TextView = view.findViewById(R.id.unSeenMessages)
//            val deleteChat: ImageView = view.findViewById(R.id.deleteChat)
            val profileImageView: ImageView = view.findViewById(R.id.profileAvatar)
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_messager, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        val otherUser = message.participants?.firstOrNull { it.id != userId }
        holder.nameTextView.text = otherUser?.fullname ?: "Unknown"
        holder.lastMessageTextView.text = message.lastMessage ?: ""
        //còn thiếu setup data vào avatar

        Log.d("Adapter Message", "Binding chat at position $position: ${message.sender.fullname}")
        if (message.unreadCount > 0) {
            holder.unreadTextView.text = message.unreadCount.toString()
            holder.unreadTextView.visibility = View.VISIBLE
        } else {
            holder.unreadTextView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            if (holder.itemView.translationX < 0) {
                ObjectAnimator.ofFloat(holder.itemView, "translationX", 0f).apply {
                    duration = 300
                    start()
                }
            } else{
                val context = it.context
                val intent = Intent(context, ChatActivity::class.java).apply {
                    putExtra("chatId", message.chatId )
                    putExtra("otherUser", message.sender.id)
                    putExtra("name", otherUser?.fullname )
                }
                context.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int = messages.size
    fun removeItem(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, messages.size)
    }
    fun updateMessages(newMessages: List<ListChat>){
        Log.d("MessageAdapter", "Updating messages: $newMessages")
        if(newMessages!= this.messages){
            this.messages = newMessages.toMutableList()
            notifyDataSetChanged()
        }
    }
    fun getChatId(position: Int): String {
        return messages[position].chatId
    }

}