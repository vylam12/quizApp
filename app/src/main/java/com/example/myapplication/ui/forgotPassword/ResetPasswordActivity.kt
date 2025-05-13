package com.example.myapplication.ui.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.AuthResetPasswordActivityBinding
import com.example.myapplication.ui.auth.LoginActivity
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.ValidationViewModel

class ResetPasswordActivity: AppCompatActivity(),View.OnFocusChangeListener, View.OnClickListener {
    private lateinit var mBinding: AuthResetPasswordActivityBinding
    private val viewModel: ValidationViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AuthResetPasswordActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        token = intent.getStringExtra("token") ?: ""
        mBinding.editTextResetPassword.onFocusChangeListener = this
        mBinding.btnResetPassword.setOnClickListener(this)
        mBinding.backLogin.setOnClickListener{
            finish()
        }
    }
    override fun onClick(v: View?){
        when(v?.id){
            R.id.btnResetPassword -> resetPassword()
        }
    }
    private fun resetPassword(){
        val password = mBinding.editTextResetPassword.text.toString()
        val confirmPassword = mBinding.editConfirmResetPassword.text.toString()
        val passwordErr = viewModel.validatePassword(password)
        val confirmPassErr = viewModel.validateConfirmPassword(confirmPassword)
        val passAndConfirmErr = viewModel.validatePasswordAndConfirmPassword(password,confirmPassword)
        val errorMessage = listOfNotNull(passwordErr, confirmPassErr, passAndConfirmErr)
            .joinToString("\n")

        if (errorMessage.isNotEmpty()) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            return
        }
        authViewModel.resetPassword(token, password)
        authViewModel.resetPasswordResponse.observe(this){res->
            if ( res?.message == "Password reset successful!"){
                Toast.makeText(this, "Password has been reset!",
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Password reset failed. Please try again!",
                    Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {}
}