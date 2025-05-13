package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.repositori.QuizResponse
import com.example.myapplication.services.RetrofitClient.quizService
import com.example.myapplication.utils.FlashcardRequest
import com.example.myapplication.utils.VocabStatus
import com.example.myapplication.utils.updateResultQuizRequest
import com.example.myapplication.utils.vocabulary
import kotlinx.coroutines.*

class QuizViewModel:ViewModel() {
    private val _generateQuizResponse = MutableLiveData<QuizResponse.GenerateQuiz?>()
    private val _getQuizResponse = MutableLiveData<QuizResponse.GetQuiz?>()
    private val _updateQuizResponse = MutableLiveData<QuizResponse.UpdateQuiz?>()
    private val _checkUserVocabulary = MutableLiveData<QuizResponse.ChechUserVocabulart?>()
    private val _getHistoryQuiz = MutableLiveData<QuizResponse.GetHistoryQuiz?>()
    private val _getVocab = MutableLiveData<QuizResponse.GetVocab?>()
    private val _updateAfterFlashcard = MutableLiveData<QuizResponse.updateAfterFlashcard?>()
    private val _getProgress = MutableLiveData<QuizResponse.GetProgress?>()
    private val _getQuestion = MutableLiveData<QuizResponse.GetQuestion?>()
    private val _getLenghtQuizResponse = MutableLiveData<QuizResponse.GetLenghtQuiz?>()


    val updateQuizResponse:  LiveData<QuizResponse.UpdateQuiz?> get()= _updateQuizResponse
    val generateQuizResponse: LiveData<QuizResponse.GenerateQuiz?> get()= _generateQuizResponse
    val getQuizResponse: LiveData<QuizResponse.GetQuiz?> get()= _getQuizResponse
    val checkUserVocabulary:  LiveData<QuizResponse.ChechUserVocabulart?> get()= _checkUserVocabulary
    val getHistoryQuiz:  LiveData<QuizResponse.GetHistoryQuiz?> get()= _getHistoryQuiz
    val getVocab:  LiveData<QuizResponse.GetVocab?> get()= _getVocab
    val updateAfterFlashcard:  LiveData<QuizResponse.updateAfterFlashcard?> get()= _updateAfterFlashcard
    val getProgress:  LiveData<QuizResponse.GetProgress?> get()= _getProgress
    val getQuestion:  LiveData<QuizResponse.GetQuestion?> get()= _getQuestion
    val getLenghtQuizResponse:  LiveData<QuizResponse.GetLenghtQuiz?> get()= _getLenghtQuizResponse

    fun getLeghtQuiz(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.getLeghtQuiz(userId)
                }
                if (res.isSuccessful) {
                    _getLenghtQuizResponse.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _getLenghtQuizResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _getLenghtQuizResponse.postValue(null)
            }
        }
    }

    fun getQuestion(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.getQuestion(userId)
                }
                if (res.isSuccessful) {
                    _getQuestion.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _getQuestion.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _getQuestion.postValue(null)
            }
        }
    }

    fun getProgress(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.getProgress(userId)
                }
                if (res.isSuccessful) {
                    _getProgress.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _getProgress.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _getProgress.postValue(null)
            }
        }
    }

    fun updateAfterFlashcard(userId: String,vocabList: List<VocabStatus>){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.updateAfterFlashcard(FlashcardRequest(userId, vocabList))
                }
                if (res.isSuccessful) {
                    _updateAfterFlashcard.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _updateAfterFlashcard.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _updateAfterFlashcard.postValue(null)
            }
        }
    }
    fun getVocab(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.getVocab(userId)
                }
                if (res.isSuccessful) {
                    _getVocab.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _getVocab.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _getVocab.postValue(null)
            }
        }
    }

    fun getHistoryQuiz(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.getHistoryQuiz(userId)
                }
                if (res.isSuccessful) {
                    _getHistoryQuiz.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _getHistoryQuiz.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _getHistoryQuiz.postValue(null)
            }
        }
    }

    fun checkUserVocabulary(userId: String){
        viewModelScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    quizService.checkUserVocabulary(mapOf("userId" to userId))
                }
                if (res.isSuccessful) {
                    _checkUserVocabulary.postValue(res.body())
                } else {
                    Log.e("QuizViewModel", "Error: ${res.errorBody()?.string()}")
                    _checkUserVocabulary.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Message failed: ${e.message}")
                _checkUserVocabulary.postValue(null)
            }
        }
    }

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
                    quizService.generateQuiz(userId)
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