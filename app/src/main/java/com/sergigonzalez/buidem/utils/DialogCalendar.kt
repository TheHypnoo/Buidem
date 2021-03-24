package com.sergigonzalez.buidem.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

object DialogCalendar {
    private val c = Calendar.getInstance()
    private var dates: String? = null
    fun dialog(_context: Context?, edt: TextView) {
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(_context!!, { _, years, months, days ->
            dates = if (months < 10 && days < 10) {
                "0" + days + "/" + "0" + (months + 1) + "/" + years
            } else if (months < 10) {
                days.toString() + "/" + "0" + (months + 1) + "/" + years
            } else if (days < 10) {
                "0" + days + "/" + (months + 1) + "/" + years
            } else {
                days.toString() + "/" + (months + 1) + "/" + years
            }
            edt.text = dates
        }, year, month, day)
        datePickerDialog.show()
    }

    fun changeFormatDate(day: String, FormatOrigin: String?, FormatFinal: String?): String {
        val parser = SimpleDateFormat(FormatOrigin, Locale.getDefault())
        val format = SimpleDateFormat(FormatFinal, Locale.getDefault())
        val date = parser.parse(day)
        return format.format(date!!)
    }
}