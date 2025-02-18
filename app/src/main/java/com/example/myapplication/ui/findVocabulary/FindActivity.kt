package com.example.myapplication.ui.findVocabulary

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.backend.repositori.TranslateResponse
import com.example.myapplication.backend.services.TranslateService
import com.example.myapplication.databinding.TestTinNhanBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FindActivity:AppCompatActivity(){
    private lateinit var mBinding : TestTinNhanBinding
    private lateinit var tinNhanText:TextView
    private lateinit var translateText:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = TestTinNhanBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        tinNhanText = mBinding.tinNhanText
        translateText =mBinding.translateText

        tinNhanText.movementMethod = LinkMovementMethod.getInstance()

        tinNhanText.setOnLongClickListener{ view ->
            showPopup(view, tinNhanText)
            println("tin nhắn ${tinNhanText.text}")
            true
        }
    }

    private fun showLookUpPopup(view:View, words:List<String>){
        val popup = PopupMenu(this, view)
        words.forEachIndexed{index, word->
            popup.menu.add(0, index, 0 ,word)
        }
        popup.setOnMenuItemClickListener { item:MenuItem->
            startVocabularyActivity(item.title.toString())
            true
        }
        popup.show()
    }
    private fun translateText(OriginalText:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://libretranslate.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service= retrofit.create(TranslateService::class.java)
        val call = service.translate(OriginalText, "en","vi")

        call.enqueue(object : Callback<TranslateResponse>{
            override fun onResponse(
                call: Call<TranslateResponse>,
                response: Response<TranslateResponse>
            ) {
                if (response.isSuccessful){
                    translateText.text = response.body()?.translatedText  ?: "Lỗi dịch"
                }
            }

            override fun onFailure(call: Call<TranslateResponse>, t: Throwable) {
                translateText.text = "Lỗi API: ${t.message}"
            }
        })

    }
    private  fun startVocabularyActivity(word:String){
        val intent = Intent(this, VocabularyActivity::class.java)
        intent.putExtra("WORD", word)
        startActivity(intent)
    }
    private fun showPopup(view: View, textView:TextView){
        val popup = PopupMenu(this,view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        val fullText = textView.text.toString()
        val words = fullText.split(" ").filter { it.isNotBlank() }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when(item.itemId){
                R.id.lookUp -> showLookUpPopup(view, words)
                R.id.translation ->{
                   translateText(tinNhanText.text.toString())
                }
                else ->false
            }
            true
        }
        popup.show()
    }
}