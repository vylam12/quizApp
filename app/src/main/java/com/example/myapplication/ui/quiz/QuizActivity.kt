package com.example.myapplication.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.QuizActivityBinding

class QuizActivity: AppCompatActivity(){
    private lateinit var mBinding:QuizActivityBinding
    private val question = arrayOf("Question?",
        "Correct?", "Wrong?", "Method?"
        )
    private  val options = arrayOf(
        arrayOf("Câu hỏi", "Bạn bè", "Người dùng", "Sai"),
        arrayOf("Trả lời", "Chính xác", "Kết bạn", "Di chuyển"),
        arrayOf("Đúng","Kết nối", "Sai", "Di chuyển"),
        arrayOf("Sai", "Phương thức", "Di chuyển", "Kết nối")
        )

    private val correctAnswers = arrayOf(0,1,2,1)
    private var currentQuestionIndex = 0
    private var score = 0
    private var wrong =0
    private var startTime: Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = QuizActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        startTime= System.currentTimeMillis() // lưu thời gian bat dau
        displayQuestion()
        mBinding.buttonOption1.setOnClickListener{
            checkAnswer(0)
        }
        mBinding.buttonOption2.setOnClickListener{
            checkAnswer(1)
        }
        mBinding.buttonOption3.setOnClickListener{
            checkAnswer(2)
        }
        mBinding.buttonOption4.setOnClickListener{
            checkAnswer(3)
        }

    }
    private fun correctButtonCollors(buttonIndex: Int){
        when(buttonIndex){
            0 ->  mBinding.buttonOption1.setBackgroundColor(Color.GREEN)
            1 ->  mBinding.buttonOption2.setBackgroundColor(Color.GREEN)
            2 ->  mBinding.buttonOption3.setBackgroundColor(Color.GREEN)
            3 ->  mBinding.buttonOption4.setBackgroundColor(Color.GREEN)
        }
    }
    private fun wrongButtonColor(buttonIndex: Int){
        when(buttonIndex){
            0 ->  mBinding.buttonOption1.setBackgroundColor(Color.RED)
            1 ->  mBinding.buttonOption2.setBackgroundColor(Color.RED)
            2 ->  mBinding.buttonOption3.setBackgroundColor(Color.RED)
            3 ->  mBinding.buttonOption4.setBackgroundColor(Color.RED)
        }
    }
    private fun resetButtonColors(){
             mBinding.buttonOption1.setBackgroundColor(Color.WHITE)
             mBinding.buttonOption2.setBackgroundColor(Color.WHITE)
             mBinding.buttonOption3.setBackgroundColor(Color.WHITE)
             mBinding.buttonOption4.setBackgroundColor(Color.WHITE)

    }

    private fun showResults(){
        val endTime = System.currentTimeMillis()
        val time = (endTime-startTime)/1000


        Toast.makeText(this, "Your score: $score out of ${question.size} ", Toast.LENGTH_LONG).show()
        val intent =  Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("wrong", wrong)
        intent.putExtra("questionSize", question.size)
        intent.putExtra("time", time)
        startActivity(intent)
    }

    private fun displayQuestion(){
        mBinding.questionText.text = question[currentQuestionIndex]
        mBinding.buttonOption1.text = options[currentQuestionIndex][0]
        mBinding.buttonOption2.text = options[currentQuestionIndex][1]
        mBinding.buttonOption3.text = options[currentQuestionIndex][2]
        mBinding.buttonOption4.text = options[currentQuestionIndex][3]
        resetButtonColors()
    }

    private fun checkAnswer(selectedAnswerIndex: Int){
        val correctAnswerIndex = correctAnswers[currentQuestionIndex]
        if (selectedAnswerIndex == correctAnswerIndex){
            score++
            correctButtonCollors(selectedAnswerIndex)
        }else{
            wrongButtonColor(selectedAnswerIndex)
            wrong++
            correctButtonCollors(correctAnswerIndex)
        }

        if (currentQuestionIndex < question.size -1){
            currentQuestionIndex++
            mBinding.questionText.postDelayed({displayQuestion()}, 1000)
        }else{
            showResults()
        }
    }
}