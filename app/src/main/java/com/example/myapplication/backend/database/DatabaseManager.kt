package com.example.myapplication.backend.database

import com.example.myapplication.BuildConfig
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.coroutine.CoroutineDatabase
//fun main(args:Array<String>){
//    val database = getDatabase()
//    runBlocking {
//        database.listCollectionNames().collect{
//            println(it)
//        }
//    }
//
//}
fun getDatabase(): CoroutineDatabase  {
//    val client = MongoClient.create(connectionString = System.getenv("MONGO_URI"))
//    val mongoUri = BuildConfig.MONGO_URI

//    val client = MongoClient.create(connectionString = mongoUri)
//    return  client.getDatabase(databaseName = "quizdatabase" )
    val mongoUri = BuildConfig.MONGO_URI + "?directConnection=true"
    val client = KMongo.createClient(mongoUri)
    return client.getDatabase("quizdatabase").coroutine
}


