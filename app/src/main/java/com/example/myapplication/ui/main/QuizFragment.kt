package com.example.myapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.model.Question
import com.example.myapplication.ui.quiz.QuizActivity
import com.example.myapplication.viewmodel.QuizViewModel
import com.example.myapplication.viewmodel.UserViewModel

class QuizFragment : Fragment() {
    private val quizViewModel: QuizViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        val btnQuiz = view.findViewById<Button>(R.id.btn_startQuiz)

        btnQuiz.setOnClickListener{
            userViewModel.userId.observe(viewLifecycleOwner) { userId ->
                if (userId.isNotEmpty()) {
                    quizViewModel.generateQuiz(userId)
                }
            }
        }

        quizViewModel.generateQuizResponse.observe(viewLifecycleOwner){res->
            res?.let {
                val intent = Intent(requireContext(),QuizActivity::class.java)
                intent.putExtra("quizId", res.quizId)
                startActivity(intent)
            }
        }

        return  view
    }

}