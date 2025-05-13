package com.example.myapplication.ui.main

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.ui.changePassword.ChangePasswordActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.utils.DateUtils
import com.example.myapplication.viewmodel.FixImage
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ProfileEditActivity :AppCompatActivity(){
    private lateinit var imageView: ImageView
    private lateinit var fixImageViewModel: FixImage
    private val PICK_IMAGE_REQUEST = 1
    private var avatarUri: Uri? = null
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var editAvatar: ImageView
    private lateinit var saveButton: Button
    private lateinit var backButton: FrameLayout
    private lateinit var userIdFB: String
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_activity)

        val userInfo = UserPreferences.getUserData(this)
        userIdFB = userInfo["id"].toString()

        val user = FirebaseAuth.getInstance().currentUser
        val providerId = user?.providerData?.get(1)?.providerId

        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val tlPass = findViewById<TextView>(R.id.txtPass)
        imageView = findViewById(R.id.profileAvatar)
        fullNameEditText = findViewById(R.id.etFullnameProfile)
        emailEditText = findViewById(R.id.editTextEmailProfile)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        dateOfBirthEditText = findViewById(R.id.etDateOfBirth)
        backButton = findViewById(R.id.backEditProfile)
        saveButton = findViewById(R.id.btnSaveProfile)
        editAvatar = findViewById(R.id.iconEditAvatar)

        fullNameEditText.setText(userInfo["fullname"])
        emailEditText.setText(userInfo["email"])
        val gender = userInfo["gender"] ?: ""
        when (gender) {
            "Male" -> radioGroupGender.check(R.id.radioMale)
            "Female" -> radioGroupGender.check(R.id.radioFemale)
            "Other" -> radioGroupGender.check(R.id.radioOther)
            else -> radioGroupGender.clearCheck()
        }
        val dob = userInfo["dob"] ?: ""
        val formattedDob = DateUtils.formatDobFromIso(dob)
        dateOfBirthEditText.setText(formattedDob)

        val avatarUrl = userInfo["avatar"]
        if (!avatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.useravatar)
                .into(imageView)
        }

        if (providerId != "password") {
            passwordLayout.visibility = View.GONE
            tlPass.visibility = View.GONE
        }

        fixImageViewModel = ViewModelProvider(this).get(FixImage::class.java)
        editAvatar.setOnClickListener{
            openGallery()
        }
        passwordLayout.setEndIconOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        backButton.setOnClickListener {
            val intent = Intent().apply {
                putExtra("updated_avatar", avatarUri.toString())
            }
            setResult(RESULT_OK, intent)
            finish()
        }
        dateOfBirthEditText.setOnClickListener {
            showMaterialDatePicker()
        }
        saveButton.setOnClickListener {
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val selectedGenderId = radioGroupGender.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                R.id.radioOther -> "Other"
                else -> ""
            }
            Log.d("ProfileData", "DOB $dateOfBirth, Gender: $gender , $avatarUri")

            userViewModel.updateUser(this, userIdFB,avatarUri, gender, dateOfBirth)
            userViewModel.updateUserResponse.observe(this) { res ->
                if (res != null) {
                    Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show()

                    val gender = res.user.gender
                    when (gender) {
                        "Male" -> radioGroupGender.check(R.id.radioMale)
                        "Female" -> radioGroupGender.check(R.id.radioFemale)
                        "Other" -> radioGroupGender.check(R.id.radioOther)
                    }
                    dateOfBirthEditText.setText( DateUtils.formatDobFromIso(res.user.birthDay))

                    UserPreferences.saveUserField(this, "user_gender", res.user.gender)
                    UserPreferences.saveUserField(this, "user_dob",
                        DateUtils.formatDobFromIso(res.user.birthDay))
                    UserPreferences.saveUserField(this, "user_avatar", res.user.avatar )
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun showMaterialDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Select date of birth")
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())

        val picker = builder.build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)

            dateOfBirthEditText.setText(formattedDate)
        }
    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                avatarUri = imageUri
                val imagePath = getRealPathFromURI(imageUri)
                if (imagePath != null) {
                    val fixedBitmap: Bitmap? = fixImageViewModel.fixImageOrientation(imagePath)
                    if (fixedBitmap != null) {
                        imageView.setImageBitmap(fixedBitmap)
                    } else {
                        imageView.setImageURI(imageUri)
                    }
                }
            }
        }
    }


    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }

}