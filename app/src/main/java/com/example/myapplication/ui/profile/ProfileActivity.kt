package com.example.myapplication.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ProfileOtherUserBinding
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.FriendsViewModel
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ProfileActivity : AppCompatActivity() {
    private lateinit var  mBinding: ProfileOtherUserBinding
    private val friendsViewModel: FriendsViewModel  by viewModels()
    private val userViewModel: UserViewModel  by viewModels()

    private lateinit var idUserOther: String
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ProfileOtherUserBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val btnMessage = findViewById<Button>(R.id.btnMessage)
        val lottieAnimationView = mBinding.loadingProfile

        lottieAnimationView.visibility = View.VISIBLE
        mBinding.layoutContent.visibility = View.GONE
        initUserInfo()
        userViewModel.getUserIFResponse.observe(this){res->
            val user = res?.user
            if (user != null) {
                mBinding.textFullName.text = user.fullname
                mBinding.textEmail.text = user.email
                if (user.avatar.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.avatar)
                        .placeholder(R.drawable.useravatar)
                        .into(mBinding.profileAvatar)
                } else {
                    mBinding.profileAvatar.setImageResource(R.drawable.useravatar)
                }
                lottieAnimationView.visibility = View.GONE
                mBinding.layoutContent.visibility = View.VISIBLE
            }else {
                Toast.makeText(this, "PROFILE LOAD FAIL", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        mBinding.btnAddFriend.visibility = View.GONE
        mBinding.btnFriend.visibility = View.VISIBLE

        mBinding.btnFriend.setOnClickListener {
            showBottomSheetUnfriend()
        }
        mBinding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUserInfo() {
        idUserOther = intent.getStringExtra("USER_ID").toString()
        userId = UserPreferences.getUserId(this) ?: ""
        userViewModel.getUserInformation(idUserOther)
    }


    private fun showBottomSheetUnfriend() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_unfriend, null)
        bottomSheet.setContentView(view)

        val btnUnfriend = view.findViewById<TextView>(R.id.btnUnfriend)
        btnUnfriend.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm unfriending")
                .setMessage("Are you sure you want to unfriend?")
                .setPositiveButton("Yes") { dialog, _ ->
                    bottomSheet.dismiss()
                    dialog.dismiss()
                    friendsViewModel.unFriend(userId,idUserOther)
                    mBinding.btnFriend.visibility = View.GONE
                    mBinding.btnAddFriend.visibility = View.VISIBLE
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        bottomSheet.show()
    }


}
