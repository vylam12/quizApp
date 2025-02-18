package com.example.myapplication.backend.repositori

import com.example.myapplication.backend.database.getDatabase
import com.example.myapplication.backend.model.Vocabulary

class VobularyRepository {
    val database = getDatabase()
    val collection = database.getCollection<Vocabulary>(collectionName = "vocabulary")
    suspend   fun saveWord(word: Vocabulary){
         collection.insertOne(word)
        val collections = database.listCollectionNames().toList()
        collections.forEach {
            println(it)
        }

    }

//  private val database : CoroutineDatabase = DatabaseManager.getDatabase() as CoroutineDatabase
//    private val wordsCollection: CoroutineCollection<Vocabulary> = database.getCollection()
//
//    suspend fun saveWord(word: Vocabulary){
//        wordsCollection.insertOne(word)
//    }
//
//    suspend fun getAllWords():List<Vocabulary>{
//        return wordsCollection.find().toList()
//    }
//
//    suspend fun findWordByText(text:String):Vocabulary?{
//        return  wordsCollection.findOne()
//    }
}