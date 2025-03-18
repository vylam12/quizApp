package com.example.myapplication.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.FriendInvitedWithSender
import com.example.myapplication.model.User
import com.example.myapplication.viewmodel.FriendsViewModel

class FriendAdapter(
    private var friends: List<Any>,
    private var isFriendList :Boolean,
    private val friendsViewModel: FriendsViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        private const val VIEW_TYPE_FRIEND= 1
        private const val VIEW_TYPE_SUGGESTION =2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isFriendList) VIEW_TYPE_FRIEND else VIEW_TYPE_SUGGESTION
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_FRIEND ->{
                val view= inflater.inflate(R.layout.viewholder_friends,parent,false)
                FriendsViewHolder(view)
            }
            VIEW_TYPE_SUGGESTION ->{
                val view= inflater.inflate(R.layout.viewholder_suggetions,parent,false)
                SuggestionViewHolder(view)
            }
            else->throw IllegalArgumentException("Unknow view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = friends[position]
        Log.d("FriendAdapter", "Bind dữ liệu: $item")
        when(holder){
            is FriendsViewHolder ->{
                if (item is User){
                    holder.name.text = item.fullname
                    Log.d("FriendAdapter", "Hiển thị bạn bè: ${item.fullname}")
                }

            }
            is SuggestionViewHolder ->{
                if (item is FriendInvitedWithSender){
                    holder.name.text = item.senderInfo.fullname

                    holder.btnDeny.setOnClickListener{
                        handleAcceptFriendInvited(idSender= item.senderId ,idReciver= item.receiverId,status = "declined")
                    }
                    holder.btnAccept.setOnClickListener{
                        handleAcceptFriendInvited(idSender= item.senderId ,idReciver= item.receiverId,status = "accepted")
                    }
                }
            }
        }
    }
    override fun getItemCount():  Int = friends.size

    private fun handleAcceptFriendInvited(idSender:String, idReciver: String,status:String){
        friendsViewModel.acceptFriendInvited(idSender, idReciver,status)

    }
    fun updateData(newFriends: List<Any>, isFriendList: Boolean) {
        Log.d("FriendAdapter", "Cập nhật dữ liệu - Danh sách: $newFriends")
        if (newFriends != this.friends) {
            this.friends = newFriends
            this.isFriendList = isFriendList
            notifyDataSetChanged()
        }
    }

    class FriendsViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.nameFriend)
        val avatar : ImageView =itemView.findViewById(R.id.profileAvatarFriend)
    }
    class SuggestionViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.nameFriendSG)
        val avatar: ImageView= itemView.findViewById(R.id.avatarFriendSG)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnDeny : Button= itemView.findViewById(R.id.btnDeny)
    }

}