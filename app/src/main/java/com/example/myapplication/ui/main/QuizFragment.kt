package com.example.myapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Flashcard
import com.example.myapplication.model.Question
import com.example.myapplication.ui.quiz.FlashcardActivity
import com.example.myapplication.ui.quiz.QuizActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.QuizViewModel

class QuizFragment : Fragment() {
    private val quizViewModel: QuizViewModel by viewModels()

    private val userId: String by lazy { UserPreferences.getUserId(requireContext()) ?: "" }
    private var vocabList: List<Flashcard>? = null
    private var questionList: List<Question>? = null
    private var quizId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        val btnQuiz = view.findViewById<Button>(R.id.btn_startQuiz)
        val textName= view.findViewById<TextView>(R.id.textNameUser)
        val quizLeghtTxt= view.findViewById<TextView>(R.id.quizLeght)
        val pointTxt= view.findViewById<TextView>(R.id.pointTxt)

        val userInfo = UserPreferences.getUserData(requireContext())
        val btnflash =  view.findViewById<Button>(R.id.btn_flashcard)
        val btnLearnFLC = view.findViewById<Button>(R.id.btnLearnFLC)
        val textAddVocab = view.findViewById<TextView>(R.id.textFlashcardHint)

        quizViewModel.getLeghtQuiz(userId)
        quizViewModel.getLenghtQuizResponse.observe(viewLifecycleOwner) { res ->
            res?.let {
                quizLeghtTxt.text = res.totalQuiz.toString()
                pointTxt.text =  res.totalPoint.toString()

            }
        }

        quizViewModel.getVocab(userId)
        quizViewModel.getVocab.observe(viewLifecycleOwner) { res ->
            res?.let {
                Log.d("QuizFragment","res make flash card $res")
                if (it.canMakeFlashcard) {
                    vocabList = it.data
                    textAddVocab.visibility = View.GONE
                    btnLearnFLC.isEnabled = true
                } else {
                    btnLearnFLC.isEnabled = false
                    textAddVocab.visibility = View.VISIBLE
                }
            }
        }


        quizViewModel.getQuestion(userId)
        quizViewModel.getQuestion.observe(viewLifecycleOwner) { res ->
            res?.let {
                Log.d("QuizFragment","res make flash card $res")
                if (it.canMakeFlashcard) {
                    questionList = it.data
                    btnflash.isEnabled = true
                } else {
                    btnflash.isEnabled = false
                }
            }
        }

        btnLearnFLC.setOnClickListener{
            FlashcardActivity.vocabList = vocabList!!
            val intent = Intent(requireContext(), FlashcardActivity::class.java)
            intent.putExtra("type", "VOCAB")
            startActivity(intent)
        }


        quizViewModel.generateQuiz(userId)
        quizViewModel.generateQuizResponse.observe(viewLifecycleOwner) { res ->
            res?.let {
                if (it.canStartQuiz) {
                    btnQuiz.isEnabled = true
                    quizId = it.quizId
                } else {
                    btnQuiz.isEnabled = false
                }
            }
        }
        btnQuiz.setOnClickListener{
            if(quizId!=null){
                val intent = Intent(requireContext(),QuizActivity::class.java)
                intent.putExtra("quizId", quizId)
                startActivity(intent)
            }
        }

        btnflash.setOnClickListener{
            FlashcardActivity.questionList = questionList!!
            val intent = Intent(context, FlashcardActivity::class.java)
            intent.putExtra("type", "QUESTION")
            startActivity(intent)

        }

        textName.text =  userInfo["fullname"]


        return  view
    }

}