package com.esc.doglend.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esc.doglend.repositories.ProfileFirebaseRepository
import com.esc.doglend.utils.calendar.data.*
import com.esc.doglend.utils.login.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Year
import java.time.YearMonth
import java.util.*
import javax.inject.Inject

private const val TAG = "myT"

@HiltViewModel
class DogProfileViewModel @Inject constructor(
    private val firebaseRepository: ProfileFirebaseRepository,
    userPreferences: UserPreferences
): ViewModel() {

    private val loginPref = userPreferences.loginPref
    private val availability = MutableLiveData<List<Available>>()
    private val monthDays = MutableLiveData<List<MonthDay>>()
    private val dogProfileChannel = Channel<ProfileViewModel.ProfileEvents>()
    val dogProfileEvent = dogProfileChannel.receiveAsFlow()
    private val weekList = mutableListOf<Available>()
    private val calendarMonths = mutableListOf<MonthDay>()
    private val previousMonths = mutableListOf<MonthDay>()
    private val nextMonths = mutableListOf<MonthDay>()
    private var visibleMonth = YearMonth.now()

    init {
        viewModelScope.launch {
            addCalendarMonths()
        }
    }

    private fun addCalendarMonths() {
        clearLists()
        calendarMonths.addAll(createCalendarData(visibleMonth))
        if (monthDays.value.isNullOrEmpty()) monthDays.postValue(calendarMonths)
        previousMonths.addAll(createCalendarData( visibleMonth.minusMonths(1)))
        nextMonths.addAll(createCalendarData( visibleMonth.plusMonths(1)))
    }

    private fun clearLists() {
        calendarMonths.clear()
        previousMonths.clear()
        nextMonths.clear()
    }

    fun addNextMonth() {
        calendarMonths.addAll(nextMonths)
        monthDays.postValue(calendarMonths)
        visibleMonth = visibleMonth.plusMonths(1)
        addCalendarMonths()
    }

    fun addPreviousMonth() {
        calendarMonths.addAll(previousMonths)
        monthDays.postValue(calendarMonths)
        visibleMonth = visibleMonth.minusMonths(1)
        addCalendarMonths()
    }

    fun setupAvailability() = viewModelScope.launch {
            loginPref.collect { mapPresetAvailability(firebaseRepository.fetchAvailability(it.uid)) }
        }

    private fun mapPresetAvailability(presetAvailability: PresetAvailability?) {
        val preset: PresetAvailability = presetAvailability ?: PresetAvailability()
        weekList.addAll(getWeekArray(preset))
        availability.postValue(weekList)
    }

    fun updateAvailability(item: Available) {
        weekList[item.position] = item
        availability.postValue(weekList)
    }
    fun saveAvailability(listAvailable: List<Available>) =viewModelScope.launch {
        loginPref.collect { firebaseRepository.updateAvailability(listAvailable, it.uid)}
    }

    fun getAvailability(): LiveData<List<Available>> { return availability }
    fun getMonthDays(): LiveData<List<MonthDay>> { return monthDays }
}