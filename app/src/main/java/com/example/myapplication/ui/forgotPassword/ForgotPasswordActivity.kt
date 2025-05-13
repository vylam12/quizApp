package com.example.myapplication.ui.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.AuthForgotPasswordActivityBinding
import com.example.myapplication.ui.BaseActivity
import com.example.myapplication.ui.auth.LoginActivity
import com.example.myapplication.utils.Result
import com.example.myapplication.viewmodel.ValidationViewModel
import com.example.myapplication.viewmodel.AuthViewModel

class ForgotPasswordActivity : BaseActivity() ,View.OnFocusChangeListener, View.OnClickListener{
    private lateinit var mBinding: AuthForgotPasswordActivityBinding
    private val viewModel: ValidationViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AuthForgotPasswordActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.editTextTextEmailAddress.onFocusChangeListener = this
        mBinding.resetPassword.setOnClickListener(this)
        mBinding.backLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?){
        when(v?.id){
            R.id.resetPassword -> hanleResetPassword()
            R.id.backLogin -> navigateTo(LoginActivity::class.java)
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {}

    private fun hanleResetPassword(){
        val email = mBinding.editTextTextEmailAddress.text.toString()
        val emailError= viewModel.validateEmail(email)

        if (emailError!= null){
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.forgotPassword(email)
        authViewModel.forgotPasswordResponse.observe(this){result->
                when(result){
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val intent = Intent(this, CheckEmailActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        val msg = result.message
                        Log.e("ForgotPasswordActivity", "Error: $msg")
                        if (msg.contains("User not found!", true)) {
                            Toast.makeText(this, "Account does not exist, please re-enter!!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {}
                }
            }

    }
    private fun navigateTo(target: Class<*>) {
        startActivity(Intent(this, target))
        finish()
    }
}