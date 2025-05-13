package com.example.myapplication.ui.main

import android.content.Intent
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Chat
import com.example.myapplication.ui.chat.ChatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.*

class MessageAdapter(
    private var messages: MutableList<Chat>,
) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){

    private var chatListener: ListenerRegistration? = null

    inner class MessageViewHolder(view:View): RecyclerView.ViewHolder(view){
        val nameTextView: TextView = view.findViewById(R.id.fullnameMessager)
        val lastMessageTextView: TextView = view.findViewById(R.id.lastMessages)
        val unreadTextView: TextView = view.findViewById(R.id.unSeenMessages)
        val profileImageView: ShapeableImageView = view.findViewById(R.id.chatAvatar)

        fun bind(chat: Chat) {
            nameTextView.text = chat.participantsInfo.firstOrNull()?.fullname ?: "Unknown"
            if(chat.unreadCount == 0){
                unreadTextView.visibility = View.GONE
            } else{
                unreadTextView.visibility = View.VISIBLE
                unreadTextView.text = chat.unreadCount.toString()
            }

            Glide.with(profileImageView.context)
                .load(chat.participantsInfo.firstOrNull()?.avatar)
                .error(R.drawable.useravatar)
                .placeholder(R.drawable.useravatar)
                .into(profileImageView)

            lastMessageTextView.text = chat.lastMessage ?: "No messages yet"

            itemView.setOnClickListener {

                val intent = Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra("CHAT_ID", chat.chatId)
                    putExtra("ID_OTHER", chat.participantsInfo.firstOrNull()?.id)
                    putExtra("AVATAR", chat.participantsInfo.firstOrNull()?.avatar)
                    putExtra("FULLNAME", chat.participantsInfo.firstOrNull()?.fullname ?: "Unknown")
                }
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_messager, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val chat = messages[position]
        holder.bind(chat)
    }
    override fun getItemCount(): Int = messages.size
    fun removeItem(position: Int) {
        messages.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, messages.size)
    }

    fun stopListening() {
        chatListener?.remove()
    }
    fun getChatId(position: Int): String {
        return messages[position].chatId
    }

    fun updateData(newChatList: List<Chat>) {
        messages.clear()
        messages.addAll(newChatList)
        notifyDataSetChanged()

    }


}