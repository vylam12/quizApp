package com.example.myapplication.ui.chat

import android.content.Context
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.repositori.UserResponse

class UserDropdownAdapter(
    context: Context,
    val users: List<UserResponse.GetUserByName>
): ArrayAdapter<UserResponse.GetUserByName>(context, 0, users) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user_dropdown, parent, false)
        val user = getItem(position)

        val avatar = view.findViewById<ImageView>(R.id.userAvatar)
        val name = view.findViewById<TextView>(R.id.userName)

        name.text = user?.fullname ?: "Unknown"

        Glide.with(context)
            .load(user?.avatar)
            .placeholder(R.drawable.useravatar)
            .error(R.drawable.useravatar)
            .into(avatar)

        return view
    }
}