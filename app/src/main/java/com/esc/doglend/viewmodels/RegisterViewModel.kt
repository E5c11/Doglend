package com.esc.doglend.viewmodels

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esc.doglend.repositories.LoginFirebaseRepository
import com.esc.doglend.utils.login.UserPreferences
import com.esc.doglend.utils.login.LoginUtils
import com.esc.doglend.utils.login.UserDetails
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "myT"

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseRepository: LoginFirebaseRepository,
    private val userPreferences: UserPreferences,
    private val geocoder: Geocoder
): ViewModel() {

    private val nameError = MutableLiveData<String>()
    private val surnameError = MutableLiveData<String>()
    private val phoneError = MutableLiveData<String>()
    private val posError = MutableLiveData<String>()
    private val locAddress = MutableLiveData<String>()
    private val registerError = MutableLiveData<String>()
    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var phone: String
    private lateinit var pos: LatLng
    private val registered = MutableLiveData<Boolean>()
    private val loginPref = userPreferences.loginPref

    fun setName(name: String) {
        val error = LoginUtils.validateName(name)
        if (error.isEmpty()) {
            this.name = name
            nameError.value = error
            Log.d(TAG, "setName: no error")
        } else {
            Log.d(TAG, "setName: error")
            nameError.value = error
        }
    }

    fun setSurname(surname: String) {
        val error = LoginUtils.validateName(surname)
        if (error.isEmpty()) {
            this.surname = surname
            surnameError.value = error
            Log.d(TAG, "setSurname: ")
        } else  surnameError.value = error
    }

    fun setPhone(phone: String) {
        val error = LoginUtils.validatePhoneNumber(phone)
        if (error.isEmpty()) {
            this.phone = phone
            phoneError.value = error
            Log.d(TAG, "setPhone: no error")
        } else  {
            Log.d(TAG, "setPhone: error")
            phoneError.value = error
        }
    }

    fun setPosition(pos: LatLng) {
        val error = LoginUtils.validatePosition(pos)
        if (error.isEmpty()) {
            this.pos = pos
            posError.value = error
            Log.d(TAG, "setPosition: ")
            getAddress()
        } else posError.value = error
    }

    private fun getAddress() {
        val addresses: List<Address> = geocoder.getFromLocation(pos.latitude, pos.longitude, 1)

        if (addresses.isNotEmpty()) {
            Log.d(TAG, "getAddress: ${addresses[0].getAddressLine(0)}")
            val address = addresses[0].getAddressLine(0)
            locAddress.value = address.substring(0, address.indexOf(","))
        }
        else posError.value = "Choose a location"
    }

    fun submitNewUser() {
        if (nameError.value!!.isNotEmpty() && surnameError.value!!.isNotEmpty() && phoneError.value!!.isNotEmpty()) {
            registered.value = false
            return
        }
        else createUser()
    }

    private fun createUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserDetails(name, surname, phone, pos.latitude, pos.longitude)
            userPreferences.updateUserData(user)
            loginPref.collect { loginPref ->
                if (loginPref.uid.isEmpty())
                    registerError.postValue("There was an error with your details")
                else {
                    firebaseRepository.setUserDetails(user, loginPref.uid)
                    registered.postValue(true)
                }
            }
        }
    }
    fun getNameError(): MutableLiveData<String> { return nameError }
    fun getSurnameError(): MutableLiveData<String> { return surnameError }
    fun getPhoneError(): MutableLiveData<String> { return phoneError }
    fun getPositionError(): MutableLiveData<String> { return posError }
    fun getLocAddress(): MutableLiveData<String> { return locAddress }
    fun getRegistered(): MutableLiveData<Boolean> { return registered }
    fun getRegisteredError(): MutableLiveData<String> { return registerError }
}