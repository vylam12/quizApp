package com.example.myapplication.ui.findVocabulary

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myapplication.R
import com.example.myapplication.databinding.TestTinNhanBinding
import com.example.myapplication.repositori.TranslateResponse
import com.example.myapplication.viewmodel.TranslateViewModel

class FindActivity:AppCompatActivity(){
    private lateinit var mBinding : TestTinNhanBinding
    private lateinit var tinNhanText:TextView
    private lateinit var translateText:TextView
    private val translateViewModel: TranslateViewModel by viewModels()
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
        translateViewModel.translate(OriginalText)

        val observer = object : Observer<TranslateResponse.Translate?> {
            override fun onChanged(res: TranslateResponse.Translate?) {
                translateViewModel.translateResponse.removeObserver(this) // Xóa observer đúng cách

                if (res != null) {
                    print("Nghĩa là : ${res.translation}")
                    translateText.text = res.translation
                } else {
                    translateText.text = "Lỗi dịch: không có phản hồi hợp lệ"
                }
            }
        }
        translateViewModel.translateResponse.observe(this, observer)
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