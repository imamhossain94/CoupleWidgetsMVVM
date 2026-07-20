package com.newagedevs.couplewidgets.extensions

import android.net.ParseException
import org.joda.time.Days
import org.joda.time.LocalDate
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

/**
 * "days until the next yearly anniversary of [inRelation]" for the widget's milestone line.
 * Returns null if [inRelation] can't be parsed (caller hides the milestone row in that case).
 */
fun nextAnniversaryText(inRelation: String?): String? {
    if (inRelation.isNullOrBlank()) return null

    return try {
        val anniversary = LocalDate.parse(inRelation)
        val today = LocalDate.now()

        var nextAnniversary = anniversary.withYear(today.year)
        if (nextAnniversary.isBefore(today)) {
            nextAnniversary = nextAnniversary.plusYears(1)
        }

        when (val daysUntil = Days.daysBetween(today, nextAnniversary).days) {
            0 -> "🎉 Happy Anniversary!"
            1 -> "🎉 Anniversary tomorrow!"
            else -> "🎉 Anniversary in $daysUntil days"
        }
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun getMidnight(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return calendar
}
