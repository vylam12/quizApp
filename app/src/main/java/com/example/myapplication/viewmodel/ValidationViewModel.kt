package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Patterns

class ValidationViewModel:ViewModel() {
    fun validateFullname(fullname:String) :String?{
        return when{
            fullname.isEmpty() -> "Fullname is required"
            else-> null
        }
    }
    fun validateEmail(email:String) :String?{
        return when{
            email.isEmpty() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email address is invalid"
            else-> null
        }
    }
    fun validatePassword(password:String):String?{
        return when{
            password.isEmpty() -> "Password is required"
            password.length <8  -> "Password must be 8 chacracters long"
            else-> null
        }

    }
    fun validateConfirmPassword(confirmPassword:String):String?{
        return when{
            confirmPassword.isEmpty() -> "Confirm password is required"
            confirmPassword.length <8  -> "Confirm password must be 8 chacracters long"
            else-> null
        }

    }
    fun validatePasswordAndConfirmPassword(password:String,confirmPassword:String):String?{
        return when{
            password!=confirmPassword-> "Confirm password doesn't match with password"
            else-> null
        }

    }
}