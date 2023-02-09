package com.newagedevs.couplewidgets.extensions

import android.net.ParseException
import org.joda.time.Period
import org.joda.time.PeriodType
import java.text.SimpleDateFormat
import java.util.*


fun parseCalendarFromString(dateString: String): Calendar {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    val calendar = Calendar.getInstance()
    if (date != null) {
        calendar.time = date
    }
    return calendar
}

fun dateDifference(_startDate: String?, _endDate: String?): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    try {

        val startDateInstance = _startDate?.let { dateFormat.parse(it) }
        val endDateInstance = _endDate?.let { dateFormat.parse(it) }

        val startDate = startDateInstance!!.time
        val endDate = endDateInstance!!.time

        // condition
        return if (startDate <= endDate) {
            val period = Period(startDate, endDate, PeriodType.yearMonthDay())
            val years: Int = period.years
            val months: Int = period.months
            val days: Int = period.days

            "${years}y ${months}m ${days}d"
        } else {
            "0y 0m 0d"
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return "0y 0m 0d"
}

