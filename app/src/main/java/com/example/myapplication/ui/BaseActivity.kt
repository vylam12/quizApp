package com.example.myapplication.ui
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity(){
    override fun dispatchTouchEvent(event : MotionEvent):Boolean{
        if (event.action ==MotionEvent.ACTION_DOWN){
            val view = currentFocus
            if(view is EditText){
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())){
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}