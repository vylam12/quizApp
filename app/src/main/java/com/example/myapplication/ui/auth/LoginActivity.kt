package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.AuthActivityLoginBinding
import com.example.myapplication.ui.BaseActivity
import com.example.myapplication.ui.auth.viewmodel.ValidationViewModel
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity: BaseActivity(), View.OnClickListener, View.OnFocusChangeListener , View.OnKeyListener {

    private lateinit var  mBinding: AuthActivityLoginBinding
    private val viewModel: ValidationViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object{
        private const val RC_SIGN_IN= 120
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        mBinding= AuthActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
        mBinding.apply {
            emailTIL.onFocusChangeListener= this@LoginActivity
            passwordTIL.onFocusChangeListener= this@LoginActivity
            btnGG.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
            textSignUp.setOnClickListener(this@LoginActivity)
        }
    }
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v!=null){
            when(v.id){
                R.id.editTextTextEmail->validateField(
                    mBinding.editTextTextEmail.text.toString(),
                    mBinding.emailTIL
                ) { viewModel.validateEmail(it) }
                R.id.editTextTextPassword->validateField(
                    mBinding.editTextTextPassword.text.toString(),
                    mBinding.passwordTIL
                ) { viewModel.validatePassword(it) }

            }
        }
    }
    private  fun validateField(value:String, textInputLayout: TextInputLayout, validator:(String)->String?){
        val errorMessage=validator(value)
        textInputLayout.apply {
            isErrorEnabled = errorMessage != null
            error= errorMessage
        }
    }
    override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btnLogin -> handleLogin()
                R.id.btnGG -> handleGoogleSignIn()
                R.id.textSignUp -> navigateTo(RegisterActivity::class.java)
                else -> Log.d("DEBUG", "Unknown view clicked")
            }
    }

    private fun handleLogin(){
        val email =   mBinding.editTextTextEmail.text.toString()
        val password= mBinding.editTextTextPassword.text.toString()

        val emailError= viewModel.validateEmail(email)
        val passError= viewModel.validatePassword(password)

        if (emailError!= null){
            Toast.makeText(this, emailError, Toast.LENGTH_SHORT).show()
            return
        }
        if (passError!= null){
            Toast.makeText(this, passError, Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful){
               firebaseAuth.currentUser?.getIdToken(true)
                   ?.addOnSuccessListener { result->
                       sendTokenToServer(result.token!!)
                   }
            }else{
                Toast.makeText(this,"Login fail: ${it.exception.toString()}" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendTokenToServer(token:String){
        authViewModel.login(this,token)

        authViewModel.loginResponse.observe(this) { res ->
            authViewModel.loginResponse.removeObservers(this)
            if (res != null) {
                Toast.makeText(this, "Login thành công!", Toast.LENGTH_SHORT).show()
                navigateTo(MainActivity::class.java)
            } else {
                Toast.makeText(this, "Xác thực Login thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun handleGoogleSignIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            val account = completedTask.result
            val idToken = account.idToken
            if (idToken!=null){
                firebaseAuthWithGoogle(account.idToken!!)
            }else{
                Toast.makeText(this, "Google Sign-In failed: ID Token is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed: ${completedTask.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun navigateTo(target: Class<*>) {
        startActivity(Intent(this, target))
    }
}