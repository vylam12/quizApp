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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
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
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object{
        private const val RC_SIGN_IN= 120
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= AuthActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        mBinding.emailTIL.onFocusChangeListener= this
        mBinding.passwordTIL.onFocusChangeListener= this
        mBinding.btnGG.setOnClickListener(this)
        mBinding.btnLogin.setOnClickListener(this)
        mBinding.textSignUp.setOnClickListener(this)

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
                R.id.btnLogin -> {
                    handleLogin()
                }
                R.id.btnGG -> {
                    handleGG()
                }
                R.id.textSignUp -> {
                    navigateToSignUp()
                }
                else -> Log.d("DEBUG", "Unknown view clicked")
            }
    }

    override fun onStart(){
        super.onStart()

        if (firebaseAuth.currentUser!=null){
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else{
            val signInIntent= Intent(this, LoginActivity::class.java)
            startActivity(signInIntent)
        }

    }
    private fun handleLogin(){
        val email =   mBinding.editTextTextEmail.text.toString()
        val password= mBinding.editTextTextPassword.text.toString()

        var isValid= true

        if (viewModel.validateEmail(email)!= null){
            val errMessage= viewModel.validateEmail(email)
            Toast.makeText(this, errMessage, Toast.LENGTH_SHORT).show()
            isValid= false
        }
        if (viewModel.validatePassword(password)!= null){
            val errMessage= viewModel.validatePassword(password)
            Toast.makeText(this, errMessage, Toast.LENGTH_SHORT).show()
            isValid= false
        }

        if(isValid){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                if (it.isSuccessful){
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private  fun handleGG(){
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
        try {
            val account = completedTask.getResult(Exception::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun navigateToSignUp() {
        val intent = Intent(applicationContext, RegisterActivity::class.java)
        startActivity(intent)
    }
}