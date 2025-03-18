package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repositori.QuizResponse
import com.example.myapplication.services.RetrofitClient.quizService
import com.example.myapplication.utils.generateQuizRequest
import com.example.myapplication.utils.updateResultQuizRequest
import com.example.myapplication.utils.vocabulary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizViewModel:ViewModel() {
    private val _generateQuizResponse = MutableLiveData<QuizResponse.GenerateQuiz?>()
    private val _getQuizResponse = MutableLiveData<QuizResponse.GetQuiz?>()
    private val _updateQuizResponse = MutableLiveData<QuizResponse.UpdateQuiz?>()

    val updateQuizResponse:  LiveData<QuizResponse.UpdateQuiz?> get()= _updateQuizResponse
    val generateQuizResponse: LiveData<QuizResponse.GenerateQuiz?> get()= _generateQuizResponse
    val getQuizResponse: LiveData<QuizResponse.GetQuiz?> get()= _getQuizResponse

    fun updateQuizResult(
        quizId:String, userId:String,
        countCorrect:Int, timeTaken:Double,
        vocabularyResults : List<vocabulary>
    ) {
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    quizService.updateQuizResult(
                        updateResultQuizRequest(
                            quizId,userId,countCorrect,timeTaken,vocabularyResults
                        ))
                }
                if (res.isSuccessful){
                    _updateQuizResponse.postValue(res.body())
                }else{
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _updateQuizResponse.postValue(null)
                }
            } catch (e: Exception) {
                _updateQuizResponse.postValue(null)
            }
        }
    }

    fun getQuiz(quizId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    quizService.getQuiz(quizId)
                }
                if (res.isSuccessful){
                    _getQuizResponse.postValue(res.body())
                }else{
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _getQuizResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _getQuizResponse.postValue(null)
            }
        }
    }
    fun generateQuiz(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO){
                    quizService.generateQuiz(generateQuizRequest(userId))
                }
                if (res.isSuccessful){
                    _generateQuizResponse.postValue(res.body())
                }else{
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _generateQuizResponse.postValue(null)
                }
            }catch (e:Exception){
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _generateQuizResponse.postValue(null)
            }
        }
    }
}