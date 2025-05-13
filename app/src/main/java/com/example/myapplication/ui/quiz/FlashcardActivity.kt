package com.example.myapplication.ui.quiz

import android.animation.*
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.example.myapplication.model.Flashcard
import com.example.myapplication.model.Question
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.utils.VocabStatus
import com.example.myapplication.viewmodel.QuizViewModel
import java.io.IOException

class FlashcardActivity : AppCompatActivity() {
    private val quizViewModel: QuizViewModel by viewModels()
    private lateinit var mode: FlashcardMode

    private lateinit var cardFront: View
    private lateinit var cardBack: View
    private lateinit var tvWord: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var tvPhonetic: TextView

    private lateinit var userIdMG: String

    private var isFront = true
    private var currentIndex = 0
    private lateinit var btnNext: Button
    private lateinit var btnAudio: View

    companion object {
        var vocabList: List<Flashcard> = emptyList()
        var questionList: List<Question> = emptyList()
    }
    enum class FlashcardMode {
        VOCAB,
        QUESTION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)
        mode = intent.getStringExtra("type")?.let {
            FlashcardMode.valueOf(it)
        } ?: FlashcardMode.VOCAB

        cardFront = findViewById(R.id.cardFront)
        cardBack = findViewById(R.id.cardBack)
        tvWord = findViewById(R.id.tvWord)
        tvMeaning = findViewById(R.id.tvMeaning)
        tvPhonetic = findViewById(R.id.tvPhonetic)

        btnNext = findViewById(R.id.btnNext)
        btnAudio = findViewById(R.id.btnAudio)
        val btnKnowWord = findViewById<TextView>(R.id.btnKnowWord)
        val userInfo = UserPreferences.getUserData(this)
        val title = findViewById<TextView>(R.id.txtTilte)

        userIdMG = userInfo["user_id"].toString()

        if (mode == FlashcardMode.VOCAB) {
            loadCard(currentIndex)
        } else if (mode == FlashcardMode.QUESTION) {
            title.text = "Review vocabulary"
            btnAudio.visibility = View.GONE
            btnKnowWord.visibility = View.GONE
            tvPhonetic.visibility = View.GONE
            loadQuestion(currentIndex)
        }

        btnKnowWord.setOnClickListener {
            vocabList[currentIndex].isKnown = true
            goToNextCard()
        }

        btnNext.setOnClickListener {
            goToNextCard()
        }
        btnAudio.setOnClickListener {
            val audioUrl = vocabList[currentIndex].audio
            if (!audioUrl.isNullOrEmpty()) {
                playAudio(audioUrl)
            }
        }
        cardFront.setOnClickListener { flipCard() }
        cardBack.setOnClickListener { flipCard() }
    }
    private fun goToNextCard() {
        if (mode == FlashcardMode.VOCAB) {
            if (currentIndex == vocabList.size - 1) {
                sendResultToServer()
                Toast.makeText(this, "Completed review!", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            currentIndex++
            loadCard(currentIndex)

            if (!isFront) flipCard()
        } else {
            if (currentIndex == questionList.size - 1) {
                sendResultToServer()
                Toast.makeText(this, "Completed review!", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            currentIndex++
            loadQuestion(currentIndex)

            if (!isFront) flipCard()
        }
    }

    private fun flipCard() {
        val scale = applicationContext.resources.displayMetrics.density
        cardFront.cameraDistance = 9000 * scale
        cardBack.cameraDistance = 9000 * scale

        val flipOut: AnimatorSet
        val flipIn: AnimatorSet

        if (isFront) {
            flipOut = AnimatorInflater.loadAnimator(this, R.animator.flip_out) as AnimatorSet
            flipIn = AnimatorInflater.loadAnimator(this, R.animator.flip_in) as AnimatorSet

            flipOut.setTarget(cardFront)
            flipIn.setTarget(cardBack)

            flipOut.start()
            flipIn.start()

            cardBack.visibility = View.VISIBLE
            cardFront.visibility = View.GONE
        } else {
            flipOut = AnimatorInflater.loadAnimator(this, R.animator.flip_out) as AnimatorSet
            flipIn = AnimatorInflater.loadAnimator(this, R.animator.flip_in) as AnimatorSet

            flipOut.setTarget(cardBack)
            flipIn.setTarget(cardFront)

            flipOut.start()
            flipIn.start()

            cardFront.visibility = View.VISIBLE
            cardBack.visibility = View.GONE
        }

        isFront = !isFront
    }
    private fun sendResultToServer() {
        val result = if (mode == FlashcardMode.VOCAB) {
            vocabList.map {
                VocabStatus(
                    vocabularyId = it.id,
                    isKnown = it.isKnown
                )
            }
        } else {
            questionList.map {
                VocabStatus(
                    vocabularyId = it.vocabulary._id,
                    isKnown = null
                )
            }
        }

        quizViewModel.updateAfterFlashcard(userIdMG, result)
    }


    private fun loadCard(index: Int) {
        if (vocabList.isEmpty()) return
        val card = vocabList[index]
        tvWord.text = card.word
        tvMeaning.text = card.meaning
        tvPhonetic.text = card.phonetic
    }
    private fun loadQuestion(index: Int) {
        val card = questionList[index]
        tvWord.text = card.content
        tvMeaning.text = card.correctAnswer
    }

    private fun playAudio(audioUrl: String) {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { it.start() }
            mediaPlayer.setOnCompletionListener { it.release() }
        } catch (e: IOException) {
            Log.e("FlashcardActivity", "Lỗi phát audio: ${e.message}")
        }
    }

}