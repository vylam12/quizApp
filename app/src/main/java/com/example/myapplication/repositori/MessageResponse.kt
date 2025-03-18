package com.example.myapplication.repositori

import com.example.myapplication.model.ListChat
import com.example.myapplication.model.Messages

sealed class MessageResponse {
    data class  CreateChat (val message:String, val chatId:String ):MessageResponse()
    data class  SendMessage (val message:String, val newMessage: Messages):MessageResponse()
    data class  GetMessage (val message:String, val mongoMessages:List<Messages> ):MessageResponse()
    data class  GetListChat(val listChat: List<ListChat> = emptyList()):MessageResponse()
    data class  CheckExistChat ( val chatId:String ):MessageResponse()
    data class  DeleteChat( val message:String ):MessageResponse()
}