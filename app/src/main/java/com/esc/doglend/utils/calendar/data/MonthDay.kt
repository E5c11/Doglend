package com.esc.doglend.utils.calendar.data

import android.util.Log
import java.time.Year
import java.time.YearMonth
import java.util.*


data class MonthDay(
    val year: Int,
    val month: Int,
    val day: Int,
    val currentMonth: Boolean,
    val booking: Booking? = null
)

fun createCalendarData(yearMonth: YearMonth): List<MonthDay> {
    val monthList = mutableListOf<MonthDay>()
    val month = yearMonth.monthValue
    val pMonth = yearMonth.minusMonths(1)
    val nMonth = yearMonth.plusMonths(1)
    Log.d("myT", "createCalendarData: $month")
    val firstDay = yearMonth.atDay( 1 ).dayOfWeek.value - 2
    val monthLength = yearMonth.lengthOfMonth()
    val pMonthLength = pMonth.month.maxLength()

    for (i in (pMonthLength - firstDay) .. pMonthLength)
        monthList.add(MonthDay(pMonth.year, pMonth.monthValue, i, false))
    for (i in 1 .. monthLength)
        monthList.add(MonthDay(yearMonth.year, yearMonth.monthValue, i, true))
    for (i in 1 .. 41 - (monthLength + firstDay))
        monthList.add(MonthDay(nMonth.year, nMonth.monthValue, i, false))

    return monthList
}

fun getMonthName(year: Int, month: Int) = YearMonth.of(year, month).month.name
