package com.example.myapplication.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.ui.findVocabulary.FindActivity
import com.example.myapplication.ui.quiz.QuizActivity


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Thread.sleep(3000)
//        installSplashScreen()
        enableEdgeToEdge()
//        setContentView(R.layout.auth_activity_login)
//        val mainView = findViewById<View>(R.id.login)
//        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        val metrics = windowManager.currentWindowMetrics.bounds
//        val screenHeight = metrics.height()

        val intent = Intent(this, FindActivity::class.java )
        startActivity(intent)
        finish()
    }

}