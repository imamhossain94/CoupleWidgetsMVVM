package com.newagedevs.couplewidgets.utils

import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.Months
import org.joda.time.Years
import org.joda.time.format.DateTimeFormat

/**
 * Turns a relationship start date into the milestones couple apps show:
 * "500 days together", "3 years", and how long until the next one.
 *
 * Everything is computed on the fly from the start date — nothing is persisted,
 * so milestones can never drift out of sync with the date the user edits.
 */
object MilestoneCalculator {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    /** Day counts worth celebrating, in the order couples tend to mark them. */
    private val DAY_MILESTONES = listOf(
        100, 200, 300, 365, 500, 730, 1000, 1095, 1460, 1500,
        1825, 2000, 2190, 2555, 2920, 3000, 3285, 3650
    )

    data class Milestone(
        val label: String,
        val date: LocalDate,
        val daysFromStart: Int,
        val reached: Boolean,
        /** Days until this milestone; negative once it has passed. */
        val daysAway: Int,
    )

    data class Togetherness(
        val days: Int,
        val years: Int,
        val months: Int,
        val remainingDays: Int,
    )

    fun parse(date: String?): LocalDate? = try {
        date?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it, formatter) }
    } catch (_: Exception) {
        null
    }

    fun format(date: LocalDate): String = date.toString(formatter)

    /** How long the couple has been together as of [today]. */
    fun togetherness(start: LocalDate, today: LocalDate = LocalDate.now()): Togetherness {
        if (start.isAfter(today)) return Togetherness(0, 0, 0, 0)

        val days = Days.daysBetween(start, today).days
        val years = Years.yearsBetween(start, today).years
        val months = Months.monthsBetween(start, today).months % 12
        val afterYearsAndMonths = start.plusYears(years).plusMonths(months)
        val remainingDays = Days.daysBetween(afterYearsAndMonths, today).days

        return Togetherness(days, years, months, remainingDays)
    }

    /**
     * Day-count and yearly-anniversary milestones around [today].
     *
     * Returns them in chronological order, each flagged as reached or upcoming.
     */
    fun milestones(start: LocalDate, today: LocalDate = LocalDate.now()): List<Milestone> {
        val daysTogether = Days.daysBetween(start, today).days

        val dayBased = DAY_MILESTONES.map { days ->
            val date = start.plusDays(days)
            Milestone(
                label = "$days days together",
                date = date,
                daysFromStart = days,
                reached = days <= daysTogether,
                daysAway = Days.daysBetween(today, date).days
            )
        }

        // Yearly anniversaries out to a decade, plus enough beyond to stay ahead
        // of long relationships.
        val yearsSoFar = Years.yearsBetween(start, today).years
        val anniversaries = (1..maxOf(10, yearsSoFar + 3)).map { year ->
            val date = start.plusYears(year)
            Milestone(
                label = if (year == 1) "1 year together" else "$year years together",
                date = date,
                daysFromStart = Days.daysBetween(start, date).days,
                reached = !date.isAfter(today),
                daysAway = Days.daysBetween(today, date).days
            )
        }

        return (dayBased + anniversaries)
            .distinctBy { it.date }
            .sortedBy { it.date }
    }

    /** The next milestone the couple will hit, or null if the list is exhausted. */
    fun nextMilestone(start: LocalDate, today: LocalDate = LocalDate.now()): Milestone? =
        milestones(start, today).firstOrNull { !it.reached }

    /**
     * The next occurrence of [date]. For yearly events (birthdays, anniversaries)
     * this rolls forward to this year or next; for one-off dates it is the date
     * itself, which may be in the past.
     */
    fun nextOccurrence(
        date: LocalDate,
        repeatsYearly: Boolean,
        today: LocalDate = LocalDate.now()
    ): LocalDate {
        if (!repeatsYearly) return date

        // Feb 29 on a non-leap year lands on Mar 1 via withYear clamping, which is
        // the behaviour most calendar apps use.
        val thisYear = safeWithYear(date, today.year)
        return if (thisYear.isBefore(today)) safeWithYear(date, today.year + 1) else thisYear
    }

    private fun safeWithYear(date: LocalDate, year: Int): LocalDate =
        try {
            date.withYear(year)
        } catch (_: Exception) {
            // Feb 29 -> Mar 1 in a non-leap year
            LocalDate(year, 3, 1)
        }

    fun daysUntil(date: LocalDate, today: LocalDate = LocalDate.now()): Int =
        Days.daysBetween(today, date).days

    /** "in 12 days" / "today" / "3 days ago" — the phrasing used across the UI. */
    fun relativeLabel(daysAway: Int): String = when {
        daysAway == 0 -> "Today"
        daysAway == 1 -> "Tomorrow"
        daysAway == -1 -> "Yesterday"
        daysAway > 0 -> "in $daysAway days"
        else -> "${-daysAway} days ago"
    }
}
