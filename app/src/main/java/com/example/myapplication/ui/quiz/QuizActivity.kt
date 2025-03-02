package com.example.myapplication.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.QuizActivityBinding

class QuizActivity: AppCompatActivity(){
    private lateinit var mBinding:QuizActivityBinding
    private lateinit var optionQuizAdapter: OptionQuizAdapter

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

        setupRecyclerView()
        displayQuestion()
    }

    private fun setupRecyclerView(){
        optionQuizAdapter = OptionQuizAdapter(emptyList()){ index, selectedOption, isChecked->
            handleOptionSelected(index, selectedOption, isChecked)

        }
        mBinding.recyclerOptions.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerOptions.adapter = optionQuizAdapter
    }

    private fun displayQuestion(){
        mBinding.questionText.text= question[currentQuestionIndex]
        mBinding.indexQuestion.text = "${currentQuestionIndex + 1}/${question.size}"

        optionQuizAdapter = OptionQuizAdapter(options[currentQuestionIndex].toList()){
          index,  selectedOption, isChecked->
            handleOptionSelected(index, selectedOption, isChecked)
        }
        mBinding.recyclerOptions.adapter = optionQuizAdapter
    }

    private fun handleOptionSelected(index: Int, selectedOption:String, isChecked:Boolean){
        val correctIndex = correctAnswers[currentQuestionIndex]
        val correctOption= options[currentQuestionIndex][correctIndex]

        if (isChecked){
            if ( selectedOption == correctOption){
                score++
                optionQuizAdapter.setAnswerResult(index, true)
            }else{
                wrong++
                optionQuizAdapter.setAnswerResult(index, false) // Đánh dấu sai
                optionQuizAdapter.setCorrectAnswerIndex(correctIndex)
            }
        }


        if (currentQuestionIndex < question.size - 1) {
            currentQuestionIndex++
            mBinding.questionText.postDelayed({ displayQuestion() }, 1000)
        } else {
            showResults()
        }
    }

    private fun showResults(){
        val endTime = System.currentTimeMillis()
        val time = (endTime-startTime)/1000

        Toast.makeText(this, "Your score: $score out of ${question.size} ", Toast.LENGTH_LONG).show()
        val intent =  Intent(this, ResultActivity::class.java)
        intent.putExtra("score", score)
        intent.putExtra("wrong", wrong)
        intent.putExtra("questionSize", question.size.toString())
        intent.putExtra("time", time)
        startActivity(intent)
    }
}