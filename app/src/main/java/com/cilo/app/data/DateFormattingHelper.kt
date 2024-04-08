package com.cilo.app.data

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.floor

class DateFormattingHelper {

    private val calendar: Calendar = Calendar.getInstance()

    fun getDateFormatter(format: String): SimpleDateFormat {
        println("formatting date")
        val dateFormatter = SimpleDateFormat(format, Locale("en_GB"))
        return dateFormatter
    }

    fun getDayInt(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getWeekInt(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun getMonthInt(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.MONTH)
    }

    fun getMonth(monthInt: Int): String {
        return DateFormatSymbols().months[monthInt - 1]
    }

    fun getYearInt(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.YEAR)
    }

    fun getYearString(date: Date, format: String): String {
        val formatter = getDateFormatter(format)
        return formatter.format(date)
    }

    fun getPeriodInt(component: Int, date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(component)
    }

    fun getSeasonInt(date: Date): Int {
        var monthInt = getMonthInt(date)
        if (monthInt == 11) {
            monthInt = 0
        }
        return floor(monthInt.toDouble() / 3).toInt()
    }

    fun getSeason(season: Int): String {
        val seasons = arrayOf("Winter", "Spring", "Summer", "Autumn")
        return seasons[season]
    }

    fun updateStartDateByOneMeasure(startDate: Date, measure: Int, goingForwards: Boolean): Date? {
        val timeToAdd = if (goingForwards) 1 else -1
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        val startDateComponents = Calendar.getInstance()
        if (measure == 0) {
            startDateComponents.add(Calendar.MONTH, timeToAdd)
        } else if (measure == 1) {
            startDateComponents.add(Calendar.MONTH, timeToAdd * 3)
        } else if (measure == 2) {
            startDateComponents.add(Calendar.YEAR, timeToAdd)
        } else {
            println("ERROR getting end date, date period type int unexpectedly found not be 0, 1, or 2.")
            return null
        }

        return calendar.time
    }

    fun getEndDate(startDate: Date, datePeriodType: Int): Date? {
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        val endDateComponents = Calendar.getInstance()
        endDateComponents.add(Calendar.SECOND, -1)

        when (datePeriodType) {
            0 -> { endDateComponents.add(Calendar.MONTH, 1) }
            1 -> { endDateComponents.add(Calendar.MONTH, 3) }
            2 -> { endDateComponents.add(Calendar.YEAR, 1) }
            else -> {
                println("ERROR getting end date, date period type int unexpectedly found not be 0, 1, or 2.")
                return null
            }
        }

        return calendar.time
    }

    fun isDate(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1

        val calendar2 = Calendar.getInstance()
        calendar2.time = date2

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    fun isDateInThisWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        calendar.time = date
        val weekOfDate = calendar.get(Calendar.WEEK_OF_YEAR)

        return currentWeek == weekOfDate
    }

    fun isDateInThisMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.time = date
        val monthOfDate = calendar.get(Calendar.MONTH)
        val yearOfDate = calendar.get(Calendar.YEAR)

        return currentYear == yearOfDate && currentMonth == monthOfDate
    }

    fun getDate1PeriodPrior(component: Int, date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(component, -1)
        return calendar.time
    }

    fun getStartOfMonth(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.time
    }
}
