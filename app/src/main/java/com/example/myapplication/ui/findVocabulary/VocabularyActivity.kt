package com.example.myapplication.ui.findVocabulary

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.backend.repositori.WordResponse
import com.example.myapplication.backend.services.DictionaryService
import com.example.myapplication.databinding.VocabularyActivityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VocabularyActivity:AppCompatActivity() {
    private lateinit var mBinding:VocabularyActivityBinding
    private var wordText: String =" "
    private var meaningText: String =" "
    private var phoneticText: String=" "
    private var exampleText: String=" "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = VocabularyActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        val word = intent.getStringExtra("WORD")?:return
        wordText = word
        fetchWorDefinition(word)


    }
    private fun fetchWorDefinition(word:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(DictionaryService::class.java)
        service.getWordDenfinition(word).enqueue(object: Callback<List<WordResponse>>{
            override fun onResponse(
                call: Call<List<WordResponse>>,
                response: Response<List<WordResponse>>
            ) {
                println("📢 Response Code: ${response.code()}") // In mã phản hồi từ API
                println("📢 Response Body: ${response.body()}")
                if (response.isSuccessful&& !response.body().isNullOrEmpty()){
                    response.body()?.firstOrNull()?.let { wordResponse ->
                        meaningText = "${wordResponse.meanings.firstOrNull()?.definitions?.firstOrNull()?.definition?:"Không có"}"
                        phoneticText = "${wordResponse.phonetic?:"Không có"}"
                        exampleText = "${wordResponse.meanings.firstOrNull()?.definitions?.firstOrNull()?.example?:"Không có"}"

                        showWord()
                    }
                } else Toast.makeText(this@VocabularyActivity, "Không tìm thấy từ này", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<WordResponse>>, t: Throwable) {
                println("Lỗi kết nối API ${t.message}")
            }
        })
    }
    private fun showWord(){
        mBinding.wordText.text = wordText
        mBinding.meaningText.text = meaningText
        mBinding.phoneticText.text = phoneticText
        mBinding.exampleText.text = exampleText

    }
}