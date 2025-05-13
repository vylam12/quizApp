package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Meaning
import com.example.myapplication.model.Vocabulary
import com.example.myapplication.repositori.TranslateResponse
import com.example.myapplication.services.RetrofitClient.translateService
import com.example.myapplication.utils.DeleteVocabularyRequest
import com.example.myapplication.utils.FindVocabularyRequest
import com.example.myapplication.utils.SaveUserVocabularyRequest
import com.example.myapplication.utils.SaveVocabularyRequest
import com.example.myapplication.utils.TranslateRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TranslateViewModel :ViewModel(){

    var savedVocabList: List<TranslateResponse.Vocab>? = null

    private val _findVocabularyResponse = MutableLiveData<TranslateResponse.FindVocabulary?>()
    private val _saveVocabularyResponse = MutableLiveData<TranslateResponse.SaveVocabulary?>()
    private val _deleteVocabularyResponse = MutableLiveData<TranslateResponse.DeleteVocabulary?>()
    private val _translateResponse = MutableLiveData<TranslateResponse.Translate?>()
    private val _getListSaveVocabResponse = MutableLiveData<TranslateResponse.GetListSaveVocab?>()
    private val _getListVocabResponse = MutableLiveData<TranslateResponse.GetListVocab?>()


    val findVocabularyResponse:LiveData<TranslateResponse.FindVocabulary?> get() = _findVocabularyResponse
    val deleteVocabularyResponse:LiveData<TranslateResponse.DeleteVocabulary?> get() = _deleteVocabularyResponse
    val saveVocabularyResponse:LiveData<TranslateResponse.SaveVocabulary?> get() = _saveVocabularyResponse
    val translateResponse:LiveData<TranslateResponse.Translate?> get() = _translateResponse
    val getListSaveVocabResponse:LiveData<TranslateResponse.GetListSaveVocab?> get() = _getListSaveVocabResponse
    val getListVocabResponse:LiveData<TranslateResponse.GetListVocab?> get() = _getListVocabResponse

    fun getListVocab(userId: String) {
        viewModelScope.launch {
            try {
                val response = translateService.getListVocab(userId)
                if (response.isSuccessful) {
                    _getListVocabResponse.postValue(response.body())
                } else {
                    Log.e("TranslateViewModel", "Error: ${response.errorBody()?.string()}")
                    _getListVocabResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Delete Vocabulary failed: ${e.message}")
                _getListVocabResponse.postValue(null)
            }
        }
    }
    fun getListSaveVocab(userId: String) {
        viewModelScope.launch {
            try {
                val response = translateService.getListSaveVocab((userId))
                if (response.isSuccessful) {
                    _getListSaveVocabResponse.postValue(response.body())
                } else {
                    Log.e("TranslateViewModel", "Error: ${response.errorBody()?.string()}")
                    _getListSaveVocabResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Delete Vocabulary failed: ${e.message}")
                _getListSaveVocabResponse.postValue(null)
            }
        }
    }

     fun translate(text: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = translateService.translate(TranslateRequest(text))

                if (response.isSuccessful) {
                    val result = response.body()?.translation ?: "Không có kết quả"
                    onResult(result)
                    _translateResponse.postValue(response.body())
                } else {
                    Log.e("TranslateViewModel", "Error: ${response.errorBody()?.string()}")
                    _translateResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Translate failed: ${e.message}")
                _translateResponse.postValue(null)
            }
        }
    }

    fun deleteVocabulary(idUserVocabulary: String) {

        viewModelScope.launch {
            try {
                val response = translateService.deleteVocabulary(DeleteVocabularyRequest(idUserVocabulary))
                if (response.isSuccessful) {
                    _deleteVocabularyResponse.postValue(response.body())
                } else {
                    Log.e("TranslateViewModel", "Error: ${response.errorBody()?.string()}")
                    _deleteVocabularyResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Delete Vocabulary failed: ${e.message}")
                _deleteVocabularyResponse.postValue(null)
            }
        }
    }
    fun saveUserVocab(userId: String,idVocab: String) {
        val request = SaveUserVocabularyRequest(userId, idVocab)
        viewModelScope.launch {
            try {
                val response = translateService.saveVocabularyById(request)
                if (response.isSuccessful) {
                    _saveVocabularyResponse.postValue(response.body())
                } else {
                    Log.e("TranslateViewModel", "Error: ${response.errorBody()?.string()}")
                    _saveVocabularyResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Save Vocabulary failed: ${e.message}")
                _saveVocabularyResponse.postValue(null)
            }
        }
    }
    // luu vocab trong tin nhắn



    fun saveVocabulary(userId: String,vocabulary: Vocabulary) {
        val request = SaveVocabularyRequest(userId, vocabulary)
        viewModelScope.launch {
            try {
                val response = translateService.saveVocabularyByData(request)
                if (response.isSuccessful) {
                    _saveVocabularyResponse.postValue(response.body())
                } else {
                    Log.e("TranslateViewModel", "Error: ${response.errorBody()?.string()}")
                    _saveVocabularyResponse.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("TranslateViewModel", "Save Vocabulary failed: ${e.message}")
                _saveVocabularyResponse.postValue(null)
            }
        }
    }

    fun findVocabulary(userId: String, word: String){
        viewModelScope.launch {
            try {
                val response = translateService.findVocabulary(FindVocabularyRequest(userId,word))
                _findVocabularyResponse.value = response
            } catch (e: HttpException) {
                _findVocabularyResponse.value = null
            } catch (e: Exception) {
                _findVocabularyResponse.value = null
            }
        }
    }
}