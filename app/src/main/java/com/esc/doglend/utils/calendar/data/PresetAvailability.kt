package com.esc.doglend.utils.calendar.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PresetAvailability(
    val monday: List<Int> = emptyList(),
    val tuesday: List<Int> = emptyList(),
    val wednesday: List<Int> = emptyList(),
    val thursday: List<Int> = emptyList(),
    val friday: List<Int> = emptyList(),
    val saturday: List<Int> = emptyList(),
    val sunday: List<Int> = emptyList()
): Parcelable {
    fun getDays(): List<List<Int>> =
        listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
}
