package com.esc.doglend.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

enum class Sex(val value: String) { MALE("Male"), FEMALE("Female")}
enum class Size(val value: String) {
    SMALL("small"), MEDIUM("Medium"), LARGE("Large"), GIANT("Giant")
}
enum class Sociability(val value: String) {
    GRUMPY("Grumpy"), SHY("Shy"), FRIENDLY("Friendly"),
    EXCITED("Excited"), ECSTATIC("Ecstatic")
}
enum class Energy(val value: String) {
    DEAD("Dead"), CALM("Calm"), NORMAL("Normal"),
    ACTIVE("Active"), HYPERACTIVE("Hyperactive")
}
enum class StaminaUnit(val value: String) { KM("km"), HOURS("hours") }

@Parcelize
//@Entity(tableName = "dog_table")
data class Dog(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "Name",
    val breed: String = "Breed",
    val sex: Sex = Sex.MALE,
    val age: Int = 5,
    val size: Size = Size.MEDIUM,
    val sociability: Sociability = Sociability.FRIENDLY,
    val energy: Energy = Energy.NORMAL,
    val stamina: Int = 5,
    @SerializedName("stamina_unit") val stamina_unit: StaminaUnit = StaminaUnit.KM,
    val image: String = "",
    val ownerUid: String = ""
) : Parcelable
