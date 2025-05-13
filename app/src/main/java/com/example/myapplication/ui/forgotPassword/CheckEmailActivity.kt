package com.example.myapplication.ui.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.AuthCheckEmailActivityBinding
import com.example.myapplication.viewmodel.AuthViewModel

class CheckEmailActivity :AppCompatActivity(){
    private lateinit var mBinding:AuthCheckEmailActivityBinding
    private lateinit var forgotPasswordAdapter: CheckEmailAdapter
    private val authViewModel: AuthViewModel by viewModels()
    private val otpLength = 5
    private lateinit var email: String
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerFinished = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AuthCheckEmailActivityBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        timerTextView = findViewById(R.id.timerTextView)

        setupRecyclerView()
        startCountdown()

        email = intent.getStringExtra("email") ?: ""
        timerTextView.setOnClickListener {
            if (isTimerFinished) {
                authViewModel.forgotPassword(email)
                Toast.makeText(this, "Sending new code...", Toast.LENGTH_SHORT).show()

                startCountdown()
            }
        }

        mBinding.backEmail.setOnClickListener{
            finish()
        }

    }

    private fun setupRecyclerView(){
        forgotPasswordAdapter = CheckEmailAdapter(otpLength)

        mBinding.recyclerOtp.layoutManager = GridLayoutManager(this, otpLength)
        mBinding.recyclerOtp.adapter = forgotPasswordAdapter
        mBinding.verifyBtn.setOnClickListener {
            val otpCode = forgotPasswordAdapter.getOtpCode()

            if (otpCode.length == otpLength) {
                authViewModel.verifyOTP(email = email, otp = otpCode)
                authViewModel.verifyOTPResponse.observe(this){res ->
                    if (res!=null){
                        val intent = Intent(this,
                            ResetPasswordActivity::class.java)
                        intent.putExtra("token", res.token)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Toast.makeText(this,
                    "Vui lòng nhập đầy đủ $otpLength số OTP", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }
    private fun startCountdown() {
        isTimerFinished = false
        timerTextView.text = "Resend in 60s"
        timerTextView.setTextColor(ContextCompat.getColor(this, R.color.md_theme_outline))
        timerTextView.isClickable = false

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timerTextView.text = "Resend in ${seconds}s"
            }

            override fun onFinish() {
                timerTextView.text = "Resend code"
                timerTextView.setTextColor(ContextCompat.getColor(this@CheckEmailActivity, R.color.md_theme_primary))
                timerTextView.isClickable = true
                isTimerFinished = true
            }
        }
        countDownTimer.start()
    }

}