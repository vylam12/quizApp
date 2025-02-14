package com.example.myapplication.backend.model
import javax.persistence.*
import org.threeten.bp.LocalDate
data class User(
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    val id:Int,
    val nameUser: String,
    val email:String,
    val avatarUrl:String?,
    val phone:String?,
    val status:String?,
    val creationDate: LocalDate =  LocalDate.now()
)
