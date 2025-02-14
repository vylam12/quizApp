package com.example.myapplication.backend.model

import org.threeten.bp.LocalDate
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

data class UserContacts(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Int,
    val userId:Int,
    val friendId: Int,
    val creationDate: LocalDate =  LocalDate.now()
)
