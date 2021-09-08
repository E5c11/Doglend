package com.esc.doglend.utils

import com.esc.doglend.entities.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

val <T> T.exhaustive: T
    get() = this

fun KClass<out Enum<*>>.enumValues() =
    this.java.enumConstants.map(Enum<*>::name).toTypedArray()

fun KClass<out Enum<*>>.enumStringValues() =
    this.java.enumConstants.map { it.name }.toTypedArray()

fun KClass<Sex>.sexStringValues() =
    this.java.enumConstants.map { it.value }.toTypedArray()

fun KClass<Size>.sizeStringValues() =
    this.java.enumConstants.map { it.value }.toTypedArray()

fun KClass<Sociability>.sociabilityStringValues() =
    this.java.enumConstants.map { it.value}.toTypedArray()

fun KClass<Energy>.energyStringValues() =
    this.java.enumConstants.map { it.value }.toTypedArray()

fun KClass<StaminaUnit>.staminaStringValues() =
    this.java.enumConstants.map { it.value }.toTypedArray()


fun <T> removeSlice(list: MutableList<T>, from: Int, end: Int) {
    list.removeAll(list.subList(from, end + 1))
}


interface OnItemClickedListener  {
    fun onItemClick()
}

