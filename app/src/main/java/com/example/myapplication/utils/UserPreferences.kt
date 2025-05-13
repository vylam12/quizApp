package com.example.myapplication.utils

import android.content.*

object  UserPreferences {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_USER_ID = "userIdMongoDB"
    private const val KEY_USER_IDFB = "userIdFirebase"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_FULLNAME = "user_fullname"
    private const val KEY_AVATAR = "user_avatar"
    private const val KEY_DOB = "user_dob"
    private const val KEY_GENDER = "user_gender"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun saveUserData(
        context: Context,
        _id: String,
        id: String,
        email: String,
        fullname: String,
        avatar: String,
        dob: String,
        gender: String
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USER_ID, _id)
            .putString(KEY_USER_IDFB, id)
            .putString(KEY_EMAIL, email)
            .putString(KEY_FULLNAME, fullname)
            .putString(KEY_AVATAR, avatar)
            .putString(KEY_DOB, dob)
            .putString(KEY_GENDER, gender)
            .apply()
    }
    fun getUserData(context: Context): Map<String, String?> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return mapOf(
            "user_id" to prefs.getString(KEY_USER_ID, null),
            "id" to prefs.getString(KEY_USER_IDFB, null),
            "email" to prefs.getString(KEY_EMAIL, null),
            "fullname" to prefs.getString(KEY_FULLNAME, null),
            "avatar" to prefs.getString(KEY_AVATAR, null),
            "dob" to prefs.getString(KEY_DOB, null),
            "gender" to prefs.getString(KEY_GENDER, null)
        )
    }

    fun getUserId(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
    fun saveUserField(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun clearUserData(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}