package com.example.chatapp.models

import java.text.SimpleDateFormat
import java.util.Locale

class Time {

    fun formatTime(timeInMillis: Long): String? {
        val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }
}