package com.example.myapplication.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.R
import com.example.myapplication.ui.auth.LoginActivity
import com.example.myapplication.utils.UserPreferences
import com.google.android.gms.auth.api.signin.*
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import com.example.myapplication.model.CompletedQuiz
import com.example.myapplication.model.LearnedWord
import com.example.myapplication.viewmodel.QuizViewModel

class ProfileFragment: Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var vocabList: List<LearnedWord> = emptyList()
    private var quizList: List<CompletedQuiz> = emptyList()

    private val quizViewModel: QuizViewModel by viewModels()

    companion object {
        private const val PROFILE_EDIT_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        val userInfo = UserPreferences.getUserData(requireContext())

        val profileName = view.findViewById<TextView>(R.id.nameProfile)
        val profileEmail = view.findViewById<TextView>(R.id.gmailProfile)
        val profileAvatar = view.findViewById<ShapeableImageView>(R.id.profileAvatar)
        val detailView = view.findViewById<TextView>(R.id.detailView)
        val linear = view.findViewById<LinearLayout>(R.id.learningProgresstxt)
        val loading = view.findViewById<LottieAnimationView>(R.id.loading_learningProgress)


        profileName.text = userInfo["fullname"]
        profileEmail.text = userInfo["email"]

        if (!userInfo["avatar"].isNullOrEmpty()) {
            Glide.with(this)
                .load(userInfo["avatar"])
                .circleCrop()
                .into(profileAvatar)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )

        quizViewModel.getProgress(userInfo["user_id"].toString())
        quizViewModel.getProgress.observe(viewLifecycleOwner){res->
            if (res!=null){
                val learnedWordsTextView = view.findViewById<TextView>(R.id.txtVocabLearn)
                val lastReviewedTextView = view.findViewById<TextView>(R.id.txtDateLearn)

                val Quiztxt = view.findViewById<TextView>(R.id.txtQuiz)

                val learnedWords = res.data.learnedWords.size
                val lastReviewed = res.data.summary.lastReviewedAt
                val completedQuizList = res.data.completedQuizzes
                val learnedWordsList = res.data.learnedWords

                loading.visibility =View.GONE
                linear.visibility = View.VISIBLE
                quizList = completedQuizList
                vocabList = learnedWordsList
                learnedWordsTextView.text = "Words learned: $learnedWords"
                Quiztxt.text = "Number of Quiz done: ${completedQuizList.size}"
                val formattedDate = lastReviewed?.let {
                    try {
                        val inputFormat = java.text
                            .SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                java.util.Locale.getDefault())
                        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                        val date = inputFormat.parse(it)
                        val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy",
                            java.util.Locale.getDefault())
                        outputFormat.format(date!!)
                    } catch (e: Exception) {
                        "Không xác định"
                    }
                } ?: "Haven't studied yet"
                lastReviewedTextView.text = "Latest school date: $formattedDate"
            }
        }
        val btnLogout = view.findViewById<FrameLayout>(R.id.btnLogout)
        val btnEditAvatar = view.findViewById<FrameLayout>(R.id.btnEditAvatar)

        btnLogout.setOnClickListener { logout() }
        btnEditAvatar.setOnClickListener {
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivityForResult(intent, PROFILE_EDIT_REQUEST_CODE)
        }
        detailView.setOnClickListener {
            if (vocabList != null && quizList != null) {
                val intent = Intent(requireContext(), LearningProgressActivity::class.java).apply {
                    putExtra("learned_words", ArrayList(vocabList!!))
                    putExtra("completed_quizzes", ArrayList(quizList!!))
                }
                startActivity(intent)
            } else {
                Log.w("ProfileFragment", "Data not ready: vocabList or quizList is null")
            }
        }
        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PROFILE_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedAvatar = data?.getStringExtra("updated_avatar")
            if (updatedAvatar != null) {
                val profileAvatar = view?.findViewById<ShapeableImageView>(R.id.profileAvatar)
                if (profileAvatar != null) {
                    Glide.with(this)
                        .load(updatedAvatar)
                        .circleCrop()
                        .into(profileAvatar)
                } else {
                    Log.e("ProfileFragment", "Profile avatar view is null")
                }
            }

        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            UserPreferences.clearUserData(requireContext())
            requireActivity().finish()
        }
    }
}