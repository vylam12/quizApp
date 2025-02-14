package com.example.myapplication.ui.findVocabulary

import android.content.Intent
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.TestTinNhanBinding

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

//    private fun getSelectedText(textView: TextView):String{
//        val text = textView.text
//       return  if (text is Spannable) {
//           val start = Selection.getSelectionStart(text)
//           val end = Selection.getSelectionEnd(text)
//           if (start != -1 && end != -1 && start != end) {
//               val spannableString = SpannableString(text)
//               val highlightColor = ContextCompat.getColor(textView.context, R.color.primary_900)
//               spannableString.setSpan(
//                   ForegroundColorSpan(highlightColor),
//                   start, end,
//                   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//               )
//               textView.text = spannableString
//               text.subSequence(start,end).toString()
//           } else {
//               " "
//           }
//       }else {
//            " "
//       }
//    }

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
//    private fun translateText(OriginalText:String):String{
//        return
//    }
    private  fun startVocabularyActivity(word:String){
        val intent = Intent(this, VocabularyActivity::class.java)
        intent.putExtra("WORD", word)
        startActivity(intent)
    }
    private fun showPopup(view: View, textView:TextView){
        val popup = PopupMenu(this,view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
//        val selectedText = getSelectedText(textView)
        val fullText = textView.text.toString()
        val words = fullText.split(" ").filter { it.isNotBlank() }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when(item.itemId){
                R.id.lookUp -> showLookUpPopup(view, words)
                R.id.translation ->{
                   startVocabularyActivity(textView.text.toString())
                }
                else ->false
            }
            true
        }
        popup.show()
    }
}