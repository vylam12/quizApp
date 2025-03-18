package com.example.myapplication.ui.chat

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Messages
import com.example.myapplication.ui.translate.VocabularyActivity
import com.example.myapplication.viewmodel.TranslateViewModel

class ChatAdapter(
    private var messages: List<Messages>,
    private val senderId: String,
    private val translateViewModel: TranslateViewModel,
    private val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    private var currentView: View? = null
    private var selectedText: String? = null
    private var lastTranslatedPosition: Int? = null

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].id_sender == senderId) VIEW_TYPE_USER else VIEW_TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_item_message_user, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_item_message_other, parent, false)
            OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setupMessageViewHolder(holder, messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.itemMessageUser)
        val translationTextView : TextView = view.findViewById(R.id.itemMessageTranslation)
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.itemMessageOther)
        val translationTextView : TextView = view.findViewById(R.id.itemMessageTranslation)
    }

    private fun translateText(originalText: String, translationTextView: TextView, dividerView: View, position: Int) {
        // Ẩn phần dịch của tin nhắn trước đó nếu có
        lastTranslatedPosition?.let {
            if (it != position) {
                notifyItemChanged(it) // Cập nhật lại item cũ để ẩn phần dịch
            }
        }

        translateViewModel.translate(originalText)
        translateViewModel.translateResponse.observeForever { res ->
            translationTextView.text = res?.translation ?: "Lỗi dịch: không có phản hồi hợp lệ"
            translationTextView.visibility = View.VISIBLE
            dividerView.visibility = View.VISIBLE
        }

        // Lưu vị trí tin nhắn vừa dịch
        lastTranslatedPosition = position
    }


    private fun getSelectedText(textView: TextView): String {
        val start = textView.selectionStart
        val end = textView.selectionEnd
        return if (start >= 0 && end > start) {
            textView.text.substring(start, end)
        } else ""
    }

    fun updateData(newData: List<Messages>) {
        val diffCallback = MessageDiffCallback(messages, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messages = newData
        diffResult.dispatchUpdatesTo(this)
    }

    class MessageDiffCallback(
        private val oldList: List<Messages>,
        private val newList: List<Messages>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    fun addMessage(newMessage: Messages) {
        val updatedList = messages.toMutableList()
        updatedList.add(newMessage)
        messages = updatedList
        notifyItemInserted(messages.size - 1)
    }

    private fun showPopup(view: View, menuItemTitle: String, action: () -> Unit) {
        val popupView = LayoutInflater.from(view.context).inflate(R.layout.popup_menu, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val menuTextView: TextView = popupView.findViewById(R.id.popupMenuText)
        menuTextView.text = menuItemTitle

        popupView.setOnClickListener {
            action()
            popupWindow.dismiss()
        }

        // Xác định hướng hiển thị dựa vào vị trí của tin nhắn
        val screenWidth = view.resources.displayMetrics.widthPixels
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val xOff: Int = if (location[0] < screenWidth / 2) {
            // Tin nhắn bên trái → Popup nằm bên phải
            view.width + 10
        } else {
            // Tin nhắn bên phải → Popup nằm bên trái
            -popupView.measuredWidth - 10
        }
        val yOff = location[1] - popupView.measuredHeight - 90

        // Hiển thị popup phía trên tin nhắn
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + xOff, yOff)
    }

    private fun showTranslatePopup(view: View, text: String, position: Int) {
        showPopup(view, "Dịch tin nhắn") {
            translateText(text, view.findViewById(R.id.itemMessageTranslation), view.findViewById(R.id.dividerUserView), position)
        }
    }
    private fun showLookUpPopup(view: View, word: String) {
        showPopup(view, "Look Up") {
            startVocabularyActivity(word)
        }
    }

    private  fun startVocabularyActivity(word:String){
        val intent = Intent(context, VocabularyActivity::class.java)
        intent.putExtra("WORD", word)
        context.startActivity(intent)
    }
    private fun setupMessageViewHolder(holder: RecyclerView.ViewHolder, message: Messages){
        val messageTextView: TextView
        val translationTextView: TextView

        if (holder is OtherMessageViewHolder){
            messageTextView = holder.messageTextView
            translationTextView = holder.translationTextView

        } else if (holder is UserMessageViewHolder){
            messageTextView = holder.messageTextView
            translationTextView = holder.translationTextView

        }else {
            return
        }
        messageTextView.text = message.translatedContent
        translationTextView.text =""

        holder.itemView.setOnLongClickListener {
            currentView = holder.itemView
            showTranslatePopup(holder.itemView, message.translatedContent, holder.adapterPosition)
            true
        }
        // xử lý chọn từ để look up
        messageTextView.setTextIsSelectable(true)
        messageTextView.setOnLongClickListener {
            currentView = holder.itemView
            selectedText = getSelectedText(messageTextView)
            if (!selectedText.isNullOrEmpty()){
                showLookUpPopup(holder.itemView, selectedText!!)
            }
            true
        }
    }
}
