package com.example.myapplication.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.CompletedQuiz
import com.example.myapplication.model.LearnedWord
import com.google.android.material.tabs.TabLayout

class LearningProgressActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var vocabAdapter: ProfileLearnedVocabAdapter
    private lateinit var quizAdapter: ProfileHistoryQuizAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learning_progress)

        recyclerView = findViewById(R.id.rcLearn)
        tabLayout = findViewById(R.id.tabLayoutFriend)

        @Suppress("UNCHECKED_CAST")
        val learnedWords = intent.getSerializableExtra("learned_words") as?
                ArrayList<LearnedWord> ?: arrayListOf()
        val completedQuizzes = intent.getSerializableExtra("completed_quizzes") as?
                ArrayList<CompletedQuiz> ?: arrayListOf()

        vocabAdapter = ProfileLearnedVocabAdapter(learnedWords)
        quizAdapter = ProfileHistoryQuizAdapter(completedQuizzes)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = vocabAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> recyclerView.adapter = vocabAdapter
                    1 -> recyclerView.adapter = quizAdapter
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
