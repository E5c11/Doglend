package com.esc.doglend.utils.login

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*

private const val TAG = "myT"
private const val centrelat: Double = -33.961147
private const val centrelng: Double = 18.710736

object LoginUtils {

    const val EMAIL_EXPRESSION = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    private const val UPPERCASE_EXPRESSION = ".*\\d.*"

    fun validatePassword(checkPass : String) : String {
        Log.d("myTagEmails", "password is: $checkPass")
        return if (checkPass.isEmpty()) {
            "Password cannot be empty"
        } else if (checkPass == checkPass.lowercase(Locale.getDefault()) && !checkPass.matches(
                UPPERCASE_EXPRESSION.toRegex())) {
            "Password must contain an uppercase and number"
        } else if (checkPass == checkPass.lowercase(Locale.getDefault())) {
            "Password must contain an uppercase"
        } else if (!checkPass.matches(UPPERCASE_EXPRESSION.toRegex())) {
            "Password must contain a number"
        } else if (checkPass.length < 6) {
            "Password must contain at least 6 characters"
        } else {
            "" //return true
        }
    }

    fun validateName(name: String): String {
        return if (name.isEmpty()) "Please enter your name"
        else ""
    }

    fun validatePhoneNumber(number: String): String {
        return if (number[0] == '+' || number[0] == '0' && number.length > 9) ""
        else "Please enter a valid number"

    }

    fun getCountryCode(number: String, context: Context): String? {
        val validatedNumber = if (number.startsWith("+")) number else "+$number"
        val phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        val phoneNumber = try {
            phoneNumberUtil.parse(validatedNumber, null)
        } catch (e: NumberParseException) {
            Log.e(TAG, "error during parsing a number")
            null
        } ?: return null

        return phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.countryCode)
    }

    fun validatePosition(pos: LatLng): String {
        val results = FloatArray(1)
        Location.distanceBetween(centrelat, centrelng, pos.latitude, pos.longitude, results)
        val distanceInMeters = results[0]
        val isWithin10km = distanceInMeters < 45000
        return if (!isWithin10km) "Please choose a valid address"
        else ""
    }
}