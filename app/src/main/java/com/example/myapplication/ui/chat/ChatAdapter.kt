package com.example.myapplication.ui.chat

import android.content.*
import android.text.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Message
import com.example.myapplication.ui.profile.ProfileActivity
import com.example.myapplication.ui.translate.VocabularyActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.TranslateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ChatAdapter(
    private var messages: List<Message>,
    private val translateViewModel: TranslateViewModel,
    private val context: Context,
    private val receiverAvatar: String,
    private val receiverId: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val userInfo = UserPreferences.getUserData(context)
    val UserIdFB = userInfo["id"]

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    private var currentView: View? = null
    private var lastTranslatedPosition: Int? = null
    private val translatedMap = mutableMapOf<Int, String>()
    private val translationInProgress = mutableMapOf<Int, Boolean>()
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == UserIdFB) VIEW_TYPE_USER else VIEW_TYPE_OTHER
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

        val translationTextView = holder.itemView.findViewById<TextView>(R.id.itemMessageTranslation)
        val dividerView = holder.itemView.findViewById<View>(R.id.dividerUserView)

        if (position == lastTranslatedPosition && translatedMap.containsKey(position)) {
            translationTextView.text = translatedMap[position]
            translationTextView.visibility = View.VISIBLE
            dividerView.visibility = View.VISIBLE
        } else {
            translationTextView.visibility = View.GONE
            dividerView.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int = messages.size

    var currentList: List<Message> = emptyList()

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.itemMessageUser)
        val translationTextView : TextView = view.findViewById(R.id.itemMessageTranslation)

    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.itemMessageOther)
        val translationTextView : TextView = view.findViewById(R.id.itemMessageTranslation)
        val avatarImageView: ImageView = view.findViewById(R.id.itemAvatarChat)

    }
    private fun translateText(originalText: String, position: Int,
                              translationTextView: TextView, dividerView: View) {
        translationInProgress[position] = true
        lastTranslatedPosition?.let { lastPos ->
            if (lastPos != position) {
                translationTextView.text = "Đang dịch..."
                notifyItemChanged(lastPos)
            }
        }
        Log.d("ChatAdapter", "originalText: $originalText")
        translationTextView.text = "Đang dịch..."
        translationTextView.visibility = View.VISIBLE
        dividerView.visibility = View.VISIBLE

        lastTranslatedPosition = position
        translateViewModel.translate(originalText) { translatedResult ->
            translatedMap[position] = translatedResult
            translationTextView.text = translatedResult
            translationInProgress[position] = false
            notifyItemChanged(position)
        }
    }

    private fun getSelectedText(textView: TextView): String {
        val start = textView.selectionStart
        val end = textView.selectionEnd
        return if (start >= 0 && end > start) {
            textView.text.substring(start, end)
        } else ""
    }

    fun updateData(newMessages: List<Message>) {
        val diffCallback = MessageDiffCallback(messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messages = newMessages
        diffResult.dispatchUpdatesTo(this)
    }


    class MessageDiffCallback(
        private val oldList: List<Message>,
        private val newList: List<Message>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].senderId == newList[newItemPosition].senderId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

        // MỞ TỪ ĐIỂN
    private  fun startVocabularyActivity(word:String){
        val intent = Intent(context, VocabularyActivity::class.java)
        intent.putExtra("WORD", word)
        context.startActivity(intent)
    }

    private fun setupMessageViewHolder(holder: RecyclerView.ViewHolder, message: Message){
        val messageTextView: TextView
        val translationTextView: TextView
        if (holder is OtherMessageViewHolder){
            messageTextView = holder.messageTextView
            translationTextView = holder.translationTextView


            if (receiverAvatar.isNotEmpty()) {
                Glide.with(context)
                    .load(receiverAvatar)
                    .placeholder(R.drawable.useravatar)
                    .into(holder.avatarImageView)
            } else {
                holder.avatarImageView.setImageResource(R.drawable.useravatar)
            }

            // xem profile
            holder.avatarImageView.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("USER_ID", receiverId)
                Log.d("ChatAdapter","receiverId: ${receiverId}")
                context.startActivity(intent)
            }

        } else if (holder is UserMessageViewHolder){
            messageTextView = holder.messageTextView
            translationTextView = holder.translationTextView
        }else {
            return
        }
        messageTextView.text = message.translatedContent
        translationTextView.text =""

        messageTextView.setTextIsSelectable(true)
        messageTextView.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val selectedText = getSelectedText(messageTextView)
                if (selectedText.isNotEmpty()) {
                    showLookUpBottomSheet(messageTextView, selectedText)
                }
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        })

        messageTextView.setOnLongClickListener {
            Log.d("ChatAdapter", "itemView long clicked at position: ${holder.adapterPosition}")
            Log.d("ChatAdapter", "message.translatedContent: ${message.translatedContent}")
            currentView = holder.itemView
            showBottomSheet(message.translatedContent, holder.adapterPosition, holder)
            true
        }

    }
    private fun showLookUpBottomSheet(messageTextView: TextView, word: String) {
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_lookup, null)
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(bottomSheetView)

        val btnLookUp = bottomSheetView.findViewById<TextView>(R.id.btnLookUp)
        btnLookUp.setOnClickListener {
            bottomSheetDialog.dismiss()
            startVocabularyActivity(word)
        }
        bottomSheetDialog.setOnDismissListener {
            messageTextView.clearFocus()
            Selection.setSelection(messageTextView.text as Spannable, 0)
        }

        bottomSheetDialog.show()
    }
    private fun showBottomSheet(text: String, position: Int,holder: RecyclerView.ViewHolder) {
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_message_actions, null)
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(bottomSheetView)

        val btnTranslate = bottomSheetView.findViewById<TextView>(R.id.btnTranslate)
        val translationTextView = holder.itemView.findViewById<TextView>(R.id.itemMessageTranslation)
        val dividerView = holder.itemView.findViewById<View>(R.id.dividerUserView)
        translationTextView.visibility = View.GONE
        dividerView.visibility = View.GONE
        btnTranslate.setOnClickListener {
            bottomSheetDialog.dismiss()
            translateText(text, position,translationTextView, dividerView )
        }

        bottomSheetDialog.show()
    }
}
