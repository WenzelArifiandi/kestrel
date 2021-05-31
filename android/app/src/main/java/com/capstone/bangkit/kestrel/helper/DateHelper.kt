package com.capstone.bangkit.kestrel.helper

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}