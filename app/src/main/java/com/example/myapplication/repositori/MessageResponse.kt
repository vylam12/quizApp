package com.example.myapplication.repositori

sealed class MessageResponse {
    data class  CreateChat (val message:String, val chatId:String, val translatedContent: String ):MessageResponse()
    data class  SendMessage (val message:String, val translatedContent: String):MessageResponse()
    data class  CheckExistChat ( val chatId:String ):MessageResponse()
    data class SearchChat(
        val filteredChats: List<FilteredChat>
    ) : MessageResponse()

    data class FilteredChat(
        val chatId: String,
        val participants: List<String>,
        val matchByMessage: List<MessageMatch>
    )

    data class MessageMatch(
        val id: String,
        val translatedContent: String,
        val timestamp: String?
    )
}