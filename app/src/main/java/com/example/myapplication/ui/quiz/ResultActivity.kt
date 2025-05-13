package com.example.myapplication.ui.quiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.QuizActivityResultBinding
import com.example.myapplication.ui.main.MainActivity

class ResultActivity :AppCompatActivity() {
    private lateinit var mBinding:QuizActivityResultBinding
    private var timeTaken: Long = 0
    private var totalQuestions :Int =0
    private var wrongAnswer:Int =0
    private var score:Int =0
    private var quizId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = QuizActivityResultBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        quizId = intent.getStringExtra("quizId")

        mBinding.btnReQuiz.setOnClickListener{
            if(quizId!=null){
                val intent = Intent(this,QuizActivity::class.java)
                intent.putExtra("quizId", quizId)
                startActivity(intent)
            }
        }
        mBinding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        score = intent.getIntExtra("score", 0)
        wrongAnswer = intent.getIntExtra("wrong", 0)
        totalQuestions =  wrongAnswer + score
        timeTaken = intent.getLongExtra("time", 0)

        mBinding.wrongText.text= "$wrongAnswer Wrong Answers"
        mBinding.totalQuestions.text= "${totalQuestions} Questions"
        mBinding.time.text = "${timeTaken} second"
        mBinding.correctText.text = "$score Correct Answers"
    }
}