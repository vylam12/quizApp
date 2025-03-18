package com.example.myapplication.ui.forgotPassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.AuthCheckEmailActivityBinding

class CheckEmailActivity :AppCompatActivity(){
    private lateinit var mBinding:AuthCheckEmailActivityBinding
    private lateinit var forgotPasswordAdapter: CheckEmailAdapter
    private val otpLength = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AuthCheckEmailActivityBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        setupRecyclerView()

    }

    private fun setupRecyclerView(){
//        val recyclerOtp = findViewById<RecyclerView>(R.id.recyclerOtp)
        forgotPasswordAdapter = CheckEmailAdapter(otpLength)

        mBinding.recyclerOtp.layoutManager = GridLayoutManager(this, otpLength)
        mBinding.recyclerOtp.adapter = forgotPasswordAdapter
//        findViewById<Button>(R.id.verifyBtn).setOnClickListener {
//            val otpCode = otpAdapter.getOtpCode()
//            // Xử lý mã OTP ở đây
//        }


    }

    private fun handleOtp(index: Int ){

    }

}