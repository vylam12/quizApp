package com.example.myapplication.ui.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.QuizActivityResultBinding

class ResultActivity :AppCompatActivity() {
    private lateinit var mBinding:QuizActivityResultBinding
    private var timeTaken: Long = 0
    private var totalQuestions :Int =0
    private var correctAnswer:Int =0
    private var wrongAnswer:Int =0
    private var score:Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = QuizActivityResultBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        score = intent.getIntExtra("score", 0)
        wrongAnswer = intent.getIntExtra("wrong", 0)
        totalQuestions = intent.getIntExtra("questionSize", 0)
        totalQuestions = intent.getIntExtra("questionSize", 0)
        timeTaken = intent.getLongExtra("time", 0)

//        mBinding.score.text = "${(score.toFloat() / totalQuestions * 100).toInt()}"
        mBinding.wrongText.text= wrongAnswer.toString()
        mBinding.totalQuestions.text= totalQuestions.toString()
        mBinding.time.text = "${timeTaken} s" 
        mBinding.correctText.text = score.toString()
    }
}