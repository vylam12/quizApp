package com.example.myapplication.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.model.Messages
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ChatAdapter(private val messages:List<Messages>, private val userId:Int):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // ViewHolder chứa các view trong item layout
        inner class ChatViewHolder(view: View): RecyclerView.ViewHolder(view){
            val textViewMessage: TextView = view.findViewById(R.id.textViewMessage)
        }

    // Tạo ViewHolder cho từng item
        override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): ChatViewHolder{
            val layout= if (viewType == VIEW_TYPE_USER){
                R.layout.item_message_user
            }else{
                R.layout.item_message_other
            }
            val view= LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ChatViewHolder(view)
        }
    // Gán dữ liệu cho ViewHolder
        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val message = messages[position]
            holder.textViewMessage.text = messages[position].content
        }
    // Xác định loại layout
        override fun getItemViewType(position: Int): Int {
            return if (messages[position].senderId== userId) VIEW_TYPE_USER else VIEW_TYPE_OTHER
        }
    // Trả về số lượng item trong danh sách
        override fun getItemCount(): Int = messages.size

    companion object{
        private  const val VIEW_TYPE_USER=1
        private  const val VIEW_TYPE_OTHER=2
    }
}