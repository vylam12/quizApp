package com.example.myapplication.model

import org.threeten.bp.LocalDate
import javax.persistence.*

data class Conversation(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Int,
    val userId:Int,
    val creationDate: LocalDate =  LocalDate.now()
)
