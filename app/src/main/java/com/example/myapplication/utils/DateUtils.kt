package com.example.myapplication.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDobFromIso(isoString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(isoString)

            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            isoString
        }
    }
}