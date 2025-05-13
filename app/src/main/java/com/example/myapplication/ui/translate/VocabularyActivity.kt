package com.example.myapplication.ui.translate

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.TranslateActivityVocabularyBinding
import com.example.myapplication.model.Vocabulary
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.TranslateViewModel

class VocabularyActivity:AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding:TranslateActivityVocabularyBinding
    private val translateViewModel: TranslateViewModel by viewModels()

    private var isBookmarked: Boolean = false
    private var currentVocabulary: Vocabulary? = null
    private var currentUserId: String = ""
    private var savedVocabularyId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TranslateActivityVocabularyBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        val userId = UserPreferences.getUserId(this)
        val word = intent.getStringExtra("WORD")?:return
        val wordText = mBinding.wordText
        val rvPhonetics = mBinding.rvPhonetics
        val rvMeaning = mBinding.rvMeanings
        val saveVocabularyButton = mBinding.saveVocabulary
        setupRecyclerViews()


        wordText.text = word
        currentUserId = userId!!
        translateViewModel.findVocabulary(userId,word)
        translateViewModel.findVocabularyResponse.observe(this){res->
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
    private fun setupRecyclerViews() {
        mBinding.rvMeanings.layoutManager = LinearLayoutManager(this)
        mBinding.rvPhonetics.layoutManager = LinearLayoutManager(this)
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
                translateViewModel.deleteVocabularyResponse.observe(this) { response ->
                    if (response != null) {
                        saveVocabularyButton.setImageResource(R.drawable.ic_bookmark)
                        isBookmarked = false
                        savedVocabularyId = null
                        showToast("Delete vocabulary successfully")
                    } else {
                        showToast("Delete vocabulary failed")
                    }
                }
            }

        }else{
            currentVocabulary?.let { vocab ->
                translateViewModel.saveVocabulary(currentUserId, vocab)
                translateViewModel.saveVocabularyResponse.observe(this) { response ->
                    if (response != null) {
                        saveVocabularyButton.setImageResource(R.drawable.ic_bookmark_full)
                        isBookmarked = true
                        showToast("Save vocabulary successfully")
                    } else {
                        showToast("Save vocabulary failed")
                    }
                }

            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}