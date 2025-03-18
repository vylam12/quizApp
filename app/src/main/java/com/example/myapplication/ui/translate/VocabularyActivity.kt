package com.example.myapplication.ui.findVocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.TranslateActivityVocabularyBinding
import kotlinx.coroutines.launch

class VocabularyActivity:AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding:TranslateActivityVocabularyBinding
    private var wordText: String =" "
    private var meaningText: String =" "
    private var phoneticText: String=" "
    private var exampleText: String=" "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TranslateActivityVocabularyBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        val word = intent.getStringExtra("WORD")?:return
        wordText = word
        fetchWorDefinition(word)
        mBinding.saveVocabulary.setOnClickListener(this)

    }
    override fun onClick(view: View?){
        when(view?.id){
//            R.id.saveVocabulary -> handleSaveVocabulary()
        }
    }
//    private fun handleSaveVocabulary(){
//        val word = Vocabulary(wordText,meaningText,phoneticText, listOf(exampleText))
//      //  VobularyRepository().saveWord(word)
//        CoroutineScope(Dispatchers.IO).launch {
//            Log.d("Database", "Inserting word: $word")
//            VobularyRepository().saveWord(word)
//            Log.d("Database", "Insert success")
//            runOnUiThread {
//                Toast.makeText(this@VocabularyActivity, "Lưu từ vựng thành công", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    private fun fetchWorDefinition(word:String){

        lifecycleScope.launch {
//            try {
//                val translateService = RetrofitClient.createService(TranslateService::class.java)
//                val res = translateService.findVocabulary(FindVocabularyRequest(word))
//
//
//            }
        }


//        service.getWordDenfinition(word).enqueue(object: Callback<List<WordResponse>>{
//            override fun onResponse(
//                call: Call<List<WordResponse>>,
//                response: Response<List<WordResponse>>
//            ) {
//                println("📢 Response Code: ${response.code()}") // In mã phản hồi từ API
//                println("📢 Response Body: ${response.body()}")
//                if (response.isSuccessful&& !response.body().isNullOrEmpty()){
//                    response.body()?.firstOrNull()?.let { wordResponse ->
//                        meaningText = "${wordResponse.meanings.firstOrNull()?.definitions?.firstOrNull()?.definition?:"Không có"}"
//                        phoneticText = "${wordResponse.phonetic?:"Không có"}"
//                        exampleText = "${wordResponse.meanings.firstOrNull()?.definitions?.firstOrNull()?.example?:"Không có"}"
//
//                        showWord()
//                    }
//                } else Toast.makeText(this@VocabularyActivity, "Không tìm thấy từ này", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onFailure(call: Call<List<WordResponse>>, t: Throwable) {
//                println("Lỗi kết nối API ${t.message}")
//            }
//        })
    }
    private fun showWord(){
        mBinding.wordText.text = wordText
        mBinding.meaningText.text = meaningText
        mBinding.phoneticText.text = phoneticText
        mBinding.exampleText.text = exampleText
    }
}