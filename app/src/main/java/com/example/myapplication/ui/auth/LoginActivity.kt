package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.AuthActivityLoginBinding
import com.example.myapplication.ui.BaseActivity
import com.example.myapplication.ui.forgotPassword.ForgotPasswordActivity
import com.example.myapplication.viewmodel.ValidationViewModel
import com.example.myapplication.ui.main.MainActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.*

class LoginActivity: BaseActivity(), View.OnClickListener, View.OnFocusChangeListener , View.OnKeyListener {

    private lateinit var mBinding: AuthActivityLoginBinding
    private val viewModel: ValidationViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
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
        setupGoogleSignIn()
        mBinding.apply {
            emailTIL.onFocusChangeListener= this@LoginActivity
            passwordTIL.onFocusChangeListener= this@LoginActivity
            btnGG.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
            textSignUp.setOnClickListener(this@LoginActivity)
            forgotPassword.setOnClickListener(this@LoginActivity)
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
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
    private fun validateField(value: String, textInputLayout: TextInputLayout, validator: (String) -> String?) {
        val errorMessage = validator(value)
        textInputLayout.error = errorMessage
    }

    override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btnLogin -> handleLogin()
                R.id.btnGG -> handleGoogleSignIn()
                R.id.textSignUp -> navigateTo(RegisterActivity::class.java)
                R.id.forgotPassword -> navigateTo(ForgotPasswordActivity::class.java)
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

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.getIdToken(false)
                        ?.addOnSuccessListener { result ->
                            sendTokenToServer(result.token!!)
                        }
                } else {
                    handleAuthError(task.exception)
                }
            }

    }

    private fun handleAuthError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException -> "Account does not exist!"
            is FirebaseAuthInvalidCredentialsException -> "Wrong password!"
            is FirebaseAuthRecentLoginRequiredException -> "Re-login required!"
            else -> "Login failed: ${exception?.localizedMessage}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun sendTokenToServer(token:String){
        Log.d("LoginActivity", "Sending token to server: $token")
        authViewModel.login(this,token)

        authViewModel.loginResponse.observe(this) { res ->
            authViewModel.loginResponse.removeObservers(this)
            if (res?.user != null) {
                val userId = res.user.id
                val avatar = res.user.avatar ?: ""
                val dob = res.user.birthDay ?: ""
                val gender = res.user.gender ?: ""
                UserPreferences.saveLoginState(this, true)
                UserPreferences.saveUserData(
                    this,
                    res.user._id,
                    userId,
                    res.user.email,
                    res.user.fullname,
                    avatar,
                    dob = dob ,
                    gender = gender,
                )
                val currentUser = FirebaseAuth.getInstance().currentUser
                Log.d("FIREBASE_USER", "UID: ${currentUser?.uid}")
                userViewModel.getUserFCMToken(userId)

                val userInfo = UserPreferences.getUserData(this)
                Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                navigateTo(MainActivity::class.java)
                finish()
            } else {
                Log.e("LoginActivity", "Server response: $res")
                Toast.makeText(this, "Xác thực Login thất bại!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Google Sign-In", "onActivityResult called with requestCode: $requestCode")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleGoogleSignIn(){
        Log.d("Google Sign-In", "Starting sign-in process")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            val account = completedTask.result
            val idToken = account.idToken
            if (idToken!=null){
                firebaseAuthWithGoogle(account.idToken!!)
            }else{
                Log.d("Login Activity","Google Sign-In failed:ID Token is null")
                Toast.makeText(this, "Login failed. Please try again.",
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("Login Activity","Google Sign-In failed: " +
                    "${completedTask.exception?.localizedMessage}")
            Toast.makeText(this, "Login failed. Please try again.",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseAuth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
                    val firebaseIdToken = result.token
                    firebaseIdToken?.let { sendTokenToServer(it) }
                }
            } else {
                Toast.makeText(this, "Authentication Failed:" +
                        " ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateTo(target: Class<*>) {
        startActivity(Intent(this, target))
    }
}