package com.example.myapplication.ui.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.QuizResultActivityBinding

class ResultActivity :AppCompatActivity() {
    private lateinit var mBinding:QuizResultActivityBinding
    private var timeTaken: Long = 0
    private var totalQuestions :Int =0
    private var correctAnswer:Int =0
    private var wrongAnswer:Int =0
    private var score:Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = QuizResultActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        score = getIntent().getIntExtra("score", 0)
        wrongAnswer = getIntent().getIntExtra("wrong", 0)
        totalQuestions = getIntent().getIntExtra("questionSize", 0)
        timeTaken = getIntent().getLongExtra("time", 0)

        mBinding.score.text = "${(score.toFloat() / totalQuestions * 100).toInt()}"
        mBinding.wrongText.text= wrongAnswer.toString()
        mBinding.totalQuestions.text= totalQuestions.toString()
        mBinding.time.text = "${timeTaken} s" 
        mBinding.correctText.text = score.toString()
    }
}