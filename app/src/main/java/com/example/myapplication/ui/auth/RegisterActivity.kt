package com.example.myapplication.ui.auth
import android.content.Intent
import android.os.Bundle
import com.example.myapplication.databinding.AuthActivityRegisterBinding
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.auth.viewmodel.ValidationViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.repositori.AuthResponse
import com.example.myapplication.ui.BaseActivity
import com.example.myapplication.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity: BaseActivity(), View.OnClickListener, View.OnFocusChangeListener ,
    View.OnKeyListener{
    //tự dong sinh ra từ layout auth_activity_register.xml, thay thế cho findViewById() bằng cách liên kết trực tiêp
    private lateinit var  mBinding: AuthActivityRegisterBinding
    private val viewModel: ValidationViewModel by viewModels()
    private lateinit var firebaseAuth:FirebaseAuth
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AuthActivityRegisterBinding.inflate dùng để liên kết lớp binding với layout.
        mBinding= AuthActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        mBinding.fullnameTIL.onFocusChangeListener= this
        mBinding.emailTIL.onFocusChangeListener= this
        mBinding.passwordTIL.onFocusChangeListener= this
        mBinding.confirmPasswordTIL.onFocusChangeListener= this

        mBinding.btnRegister.setOnClickListener(this)
        mBinding.textSignIn.setOnClickListener(this)
        mBinding.btnGoogle.setOnClickListener(this)
    }
    //kiểm tra các sự kiện nhấn phím nếu cần.
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
    //Khi focus thay đổi trên một View:
        if (v!=null){
            when(v.id){
                R.id.editTextTextFullname->validateField(
                    mBinding.editTextTextFullname.text.toString(),
                    mBinding.fullnameTIL
                ) { viewModel.validateFullname(it) }
                R.id.editTextTextEmail->validateField(
                    mBinding.editTextTextEmail.text.toString(),
                    mBinding.emailTIL
                ) { viewModel.validateEmail(it) }
                R.id.editTextTextPassword->validateField(
                    mBinding.editTextTextPassword.text.toString(),
                    mBinding.passwordTIL
                ) { viewModel.validatePassword(it) }
                R.id.editTextTextConfirmPassword->validateField(
                    mBinding.editTextTextConfirmPassword.text.toString(),
                    mBinding.confirmPasswordTIL
                ) { viewModel.validateConfirmPassword(it) }
            }
        }
    }

    private  fun validateField(value:String,textInputLayout: TextInputLayout, validator:(String)->String?){
        val errorMessage=validator(value)
        textInputLayout.apply {
            isErrorEnabled = errorMessage != null
            error= errorMessage
        }
    }
    //bắt các sự kiện click như click nút Register, Cancel
    override fun onClick(v: View?) {
        if(v!= null){
            when(v.id){
                R.id.btnRegister-> handleRegister()
                R.id.textSignIn-> navigateToSignIn()
            }
        }
    }
    private fun navigateToSignIn(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }
    private fun handleRegister(){
        val fullname = mBinding.editTextTextFullname.text.toString()
        val email= mBinding.editTextTextEmail.text.toString()
        val password =mBinding.editTextTextPassword.text.toString()
        val confirmPassword = mBinding.editTextTextConfirmPassword.text.toString()

        val fullnameError = viewModel.validateFullname(fullname)
        val emailError= viewModel.validateEmail(email)
        val passwordError= viewModel.validatePassword(password)
        val confirmPasswordError= viewModel.validateConfirmPassword(confirmPassword)

        if (fullnameError!=null ||emailError!=null ||passwordError!=null ||confirmPasswordError!=null ){
            val errorMessage = fullnameError?:emailError ?:passwordError ?:confirmPasswordError
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.register(email,password,fullname)

        val observer = object : Observer<AuthResponse.Register?> {
            override fun onChanged(response: AuthResponse.Register?) {
                authViewModel.registerResponse.removeObserver(this)

                if (response != null) {
                    Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    navigateTo(LoginActivity::class.java)
                } else {
                    Toast.makeText(this@RegisterActivity, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        authViewModel.registerResponse.observe(this, observer)
    }
    private fun navigateTo(target: Class<*>) {
        startActivity(Intent(this, target))
    }

}

