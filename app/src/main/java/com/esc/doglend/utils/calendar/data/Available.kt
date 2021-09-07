package com.esc.doglend.utils.calendar.data

import android.os.Parcelable
import com.esc.doglend.utils.calendar.data.PresetAvailability
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

enum class WeekDays(val value: String) {
    TIME("time"), MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"), FRIDAY("Friday"), SATURDAY("Saturday"), SUNDAY("Sunday")
}

@Parcelize
data class Available(
    val day: WeekDays,
    val time: Int?,
    val position: Int,
    val available: Boolean = true
) : Parcelable

fun KClass<WeekDays>.weekDaysStringValues() =
    this.java.enumConstants.map { it.value }.toTypedArray()

fun changeTimeFormat(time: Int): String {
    val displayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val parseFormat = SimpleDateFormat("hh", Locale.getDefault())
    val date: Date = parseFormat.parse(time.toString())
    return displayFormat.format(date)
}

fun getWeekArray(preset: PresetAvailability): List<Available> {
    val list = mutableListOf<Available>()
    var time = 5
    for (i in 0..135) {
        when {
            (i%8==1) -> list.add(Available(WeekDays.MONDAY, time, i, preset.getDays()[0].contains(time)))
            (i%8==2) -> list.add(Available(WeekDays.TUESDAY, time, i, preset.getDays()[1].contains(time)))
            (i%8==3) -> list.add(Available(WeekDays.WEDNESDAY, time, i, preset.getDays()[2].contains(time)))
            (i%8==4) -> list.add(Available(WeekDays.THURSDAY, time, i, preset.getDays()[3].contains(time)))
            (i%8==5) -> list.add(Available(WeekDays.FRIDAY, time, i, preset.getDays()[4].contains(time)))
            (i%8==6) -> list.add(Available(WeekDays.SATURDAY, time, i, preset.getDays()[5].contains(time)))
            (i%8==7) -> list.add(Available(WeekDays.SUNDAY, time, i, preset.getDays()[6].contains(time)))
            else -> {
                list.add(Available(WeekDays.TIME, time, i))
                time++
            }
        }
    }
    return list
}
