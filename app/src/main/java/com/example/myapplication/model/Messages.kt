package com.example.myapplication.model

import org.threeten.bp.LocalDate
import javax.persistence.*

data class Messages(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Int? =null,
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    val conversation: Conversation?=null,
    val senderId: Int,
    val content:String,
    val isRead:Boolean,
    val creationDate: LocalDate? =  LocalDate.now()
)
