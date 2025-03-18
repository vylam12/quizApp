package com.example.myapplication.ui.forgotPassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.AuthForgotPasswordActivityBinding
import com.example.myapplication.viewmodel.ValidationViewModel
import com.example.myapplication.viewmodel.AuthViewModel

class ForgotPasswordActivity :AppCompatActivity() ,View.OnFocusChangeListener, View.OnClickListener{
    private lateinit var mBinding: AuthForgotPasswordActivityBinding
    private val viewModel: ValidationViewModel by viewModels()
    val authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AuthForgotPasswordActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.editTextTextEmailAddress.onFocusChangeListener = this
        mBinding.resetPassword.setOnClickListener(this)
    }

    override fun onClick(v: View?){
        when(v?.id){
            R.id.resetPassword -> hanleResetPassword()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
       if (v!= null){
           when(v.id){
//               R.id.editTextTextEmailAddress -> validateField()
           }
       }
    }

    private fun hanleResetPassword(){
        val email = mBinding.editTextTextEmailAddress.text.toString()
        val emailError= viewModel.validateEmail(email)

        if (emailError!= null){
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel

    }

}