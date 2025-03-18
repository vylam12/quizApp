package com.example.myapplication.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.bottom_message ->{
                    replaceFrament(MessagerFragment())
                    true
                }
                R.id.bottom_friend ->{
                    replaceFrament( FriendFragment())
                    true
                }
                R.id.bottom_save ->{
                    replaceFrament(SaveFragment())
                    true
                }
                R.id.bottom_quiz ->{
                    replaceFrament( QuizFragment())
                    true
                }
                R.id.bottom_profile ->{
                    replaceFrament(FrofileFragment())
                    true
                }
                else -> false
            }
        }
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.bottom_message
        }
    }

    private fun replaceFrament(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit()

    }
}