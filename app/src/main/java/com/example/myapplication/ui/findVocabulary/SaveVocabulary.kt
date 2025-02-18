//package com.example.myapplication.ui.findVocabulary
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.myapplication.backend.database.DatabaseManager
//import com.example.myapplication.backend.repositori.VobularyRepository
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class SaveVocabulary :AppCompatActivity(){
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        loadSaveVocabulary()
//    }
//
//    private fun  loadSaveVocabulary(){
//        CoroutineScope(Dispatchers.IO).launch {
//            val words = VobularyRepository().getAllWords()
//            words.forEach{
//                println("📚 WORD SAVE ${it.word}: ${it.meaning}")
//            }
//        }
//    }
//}