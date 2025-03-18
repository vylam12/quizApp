package com.example.myapplication.ui.translate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.TranslateActivityVocabularyBinding
import com.example.myapplication.model.Vocabulary
import com.example.myapplication.viewmodel.TranslateViewModel
import com.example.myapplication.viewmodel.UserViewModel

class VocabularyActivity:AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding:TranslateActivityVocabularyBinding
    private val translateViewModel: TranslateViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var isBookmarked: Boolean = false
    private var currentVocabulary: Vocabulary? = null
    private var currentUserId: String = ""
    private var savedVocabularyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TranslateActivityVocabularyBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        val word = intent.getStringExtra("WORD")?:return
        val wordText = mBinding.wordText
        val rvPhonetics = mBinding.rvPhonetics
        val rvMeaning = mBinding.rvMeanings
        val saveVocabularyButton = mBinding.saveVocabulary
        rvMeaning.layoutManager = LinearLayoutManager(this)
        rvPhonetics.layoutManager = LinearLayoutManager(this)

        wordText.text = word

        userViewModel.userId.observe(this) { userId ->
            if (userId.isNotEmpty()) {
                currentUserId = userId
                translateViewModel.findVocabulary(userId,word)
            }
        }
        translateViewModel.findVocabularyResponse.observe(this){res->
            Log.d("VocabularyActivity", "Response: $res")
            val vocabulary = res?.newWord
            val userSaved = res?.userSaved

            currentVocabulary = vocabulary
            isBookmarked = userSaved != null
            savedVocabularyId = userSaved
            if (isBookmarked) {
                saveVocabularyButton.setImageResource(R.drawable.ic_bookmark_full)
            }else{
                saveVocabularyButton.setImageResource(R.drawable.ic_bookmark)
            }
            if (vocabulary != null) {
                rvPhonetics.adapter = PhoneticAdapter(vocabulary.phonetics)
                rvMeaning.adapter = MeaningAdapter(vocabulary.meanings)
            }
        }
        mBinding.saveVocabulary.setOnClickListener(this)
    }
    override fun onClick(view: View?){
        when(view?.id){
           R.id.saveVocabulary -> handleSaveVocabulary()
        }
    }
    private fun handleSaveVocabulary(){
        val saveVocabularyButton: ImageButton = mBinding.saveVocabulary
        if (isBookmarked){
            savedVocabularyId?.let { id ->
                translateViewModel.deleteVocabulary(id)
            }
            saveVocabularyButton.setImageResource(R.drawable.ic_bookmark)
            isBookmarked = false
            savedVocabularyId = null
        }else{
            currentVocabulary?.let { vocab ->
                translateViewModel.saveVocabulary(currentUserId, vocab)
                saveVocabularyButton.setImageResource(R.drawable.ic_bookmark_full)
                isBookmarked = true
            }
        }
    }
}