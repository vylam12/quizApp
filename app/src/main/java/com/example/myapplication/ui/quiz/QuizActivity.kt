package com.example.myapplication.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.QuizActivityBinding
import com.example.myapplication.model.Question
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.utils.vocabulary
import com.example.myapplication.viewmodel.QuizViewModel

class QuizActivity: AppCompatActivity(){
    private lateinit var mBinding:QuizActivityBinding
    private lateinit var optionQuizAdapter: OptionQuizAdapter
    private val quizViewModel: QuizViewModel by viewModels()

    private var quizId: String? = null
    private var quizQuestions: List<Question> = emptyList()
    private var userId: String? = null

    private var currentQuestionIndex = 0
    private var score = 0
    private var wrong =0
    private var startTime: Long=0

    private  val correctAnswersByVocabulary = mutableMapOf<String, Int>()
    private val wrongAnswersByVocabulary = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = QuizActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        initData()
        observeViewModel()
        setupRecyclerView()
    }

    private fun initData(){
        startTime= System.currentTimeMillis()

        quizId = intent.getStringExtra("quizId")

        userId = UserPreferences.getUserId(this)  ?: ""
        mBinding.loading.visibility = View.VISIBLE
        mBinding.contentLayout.visibility = View.GONE

        quizViewModel.getQuiz(quizId!!)
    }

    private fun observeViewModel(){
        quizViewModel.getQuizResponse.observe(this){res->
            mBinding.loading.visibility = View.GONE

            if (res != null) {
                quizQuestions = res.listQuestion
                mBinding.contentLayout.visibility = View.VISIBLE
                displayQuestion()
            }else {
                Toast.makeText(this, "No questions asked!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun setupRecyclerView(){
        optionQuizAdapter = OptionQuizAdapter(emptyList()){ index, selectedOption, isChecked->
            handleOptionSelected(index, selectedOption, isChecked)

        }
        mBinding.recyclerOptions.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerOptions.adapter = optionQuizAdapter
    }

    private fun displayQuestion(){
        val currentQuestion = quizQuestions[currentQuestionIndex]

        mBinding.questionText.text= currentQuestion.content
        mBinding.indexQuestion.text ="${currentQuestionIndex + 1}/${quizQuestions.size}"

        optionQuizAdapter = OptionQuizAdapter(currentQuestion.options){
            index, selectedOption, isChecked ->
            handleOptionSelected(index, selectedOption, isChecked)
        }
        mBinding.recyclerOptions.adapter = optionQuizAdapter
    }

    private fun handleOptionSelected(index: Int, selectedOption:String, isChecked:Boolean){
        val currentQuestion = quizQuestions[currentQuestionIndex]
        val correctAnswer= currentQuestion.correctAnswer
        val correctIndex = currentQuestion.options.indexOf(correctAnswer)

        if (isChecked){
            val  vocabId = currentQuestion.vocabulary._id
            Log.d("QuizActivity", "Đang xử lý câu hỏi có vocabId: $vocabId")
            if ( selectedOption == correctAnswer){
                score++
                optionQuizAdapter.setAnswerResult(index, true)

                correctAnswersByVocabulary[vocabId] = correctAnswersByVocabulary.getOrDefault(vocabId,0)+1
            }else{
                wrong++
                optionQuizAdapter.setAnswerResult(index, false)
                optionQuizAdapter.setCorrectAnswerIndex(correctIndex)

                wrongAnswersByVocabulary[vocabId] = wrongAnswersByVocabulary.getOrDefault(vocabId, 0) + 1
            }
            Log.d("QuizActivity", "correctAnswersByVocabulary: $correctAnswersByVocabulary")
            Log.d("QuizActivity", "wrongAnswersByVocabulary: $wrongAnswersByVocabulary")
        }

        if (currentQuestionIndex < quizQuestions.size - 1) {
            currentQuestionIndex++
            mBinding.questionText.postDelayed({ displayQuestion() }, 1000)
        } else {
            showResults()
        }
    }

    private fun showResults(){
        val endTime = System.currentTimeMillis()
        val time = (endTime-startTime)/1000

        Toast.makeText(this, "Your score: $score out of ${quizQuestions.size} ", Toast.LENGTH_LONG).show()

        sendResultsToServer()

        val intent =  Intent(this, ResultActivity::class.java)
        intent.putExtra("quizId", quizId)
        intent.putExtra("score", score)
        intent.putExtra("wrong", wrong)
        intent.putExtra("questionSize", quizQuestions.size.toString())
        intent.putExtra("time", time)
        startActivity(intent)
    }
    private fun sendResultsToServer() {
        val vocabularyResults = correctAnswersByVocabulary.keys.union(wrongAnswersByVocabulary.keys)
            .map { vocabId ->
            vocabulary(
                vocabId,
                correctAnswersByVocabulary.getOrDefault(vocabId, 0),
                wrongAnswersByVocabulary.getOrDefault(vocabId, 0)
            )
        }
        Log.d("QuizActivity", "vocabularyResults: $vocabularyResults")
        quizViewModel.updateQuizResult(
            quizId = quizId!!, userId = userId!!,
            countCorrect = score,
            timeTaken = (System.currentTimeMillis() - startTime) / 1000.0,
            vocabularyResults = vocabularyResults
        )
        finish()
    }

}