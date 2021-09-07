package com.esc.doglend.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esc.doglend.repositories.LoginFirebaseRepository
import com.esc.doglend.utils.login.GUEST_EMAIL
import com.esc.doglend.utils.login.UserPreferences
import com.esc.doglend.utils.login.LoginUtils
import com.esc.doglend.utils.login.LoginUtils.EMAIL_EXPRESSION
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

private const val TAG = "myT"
private const val LOGGED_IN = "logged_in"
private const val USER_NOT_FOUND = "user_not_found"
private const val GUEST_USER = "guest_user"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: LoginFirebaseRepository,
    private val userPreferences: UserPreferences
) : ViewModel()  {

    private var validEmail = false
    private var newUser = false
    private val emailError = MutableLiveData<String>()
    private val passwordError = MutableLiveData<String>()
    private val passConError = MutableLiveData<String>()
    private val progressBar = MutableLiveData<Int>()
    private val loginError = MutableLiveData<String>()
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passCon: String
    private val loggedIn = MutableLiveData<Boolean>()
    private val loginPref = userPreferences.loginPref
    private val loginChannel = Channel<LoginEvents>()
    val loginEvent = loginChannel.receiveAsFlow()

    init {
        viewModelScope.launch { checkLogin() }
    }

    private suspend fun checkLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            loginPref.collect {
                if (it.email != GUEST_EMAIL) loginChannel.send(LoginEvents.LoggedInEvent)
                else loginChannel.send(LoginEvents.UserNotFoundEvent)
            }
        }.cancelAndJoin()
    }

    fun setNewUser(new: Boolean) {newUser = new}

    fun isValidEmail(tEmail : String) {
        val pattern = Pattern.compile(EMAIL_EXPRESSION, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(tEmail)
        if (matcher.matches()) {
            viewModelScope.launch(Dispatchers.IO) {
                if (!firebaseRepository.authenticate(tEmail)) {
                    if (newUser)
                        emailError.postValue("Email exists")
                    else emailError.postValue("")
                } else if (!newUser) emailError.postValue("User does not exist")
                validEmail = true
            }
        }
    }

    private fun validateEmail(): Boolean {
        return if (email.isEmpty()) {
            emailError.value = "Email can not be empty"
            false
        } else if (!validEmail) {
            emailError.value = "Please enter a valid email"
            false
        } else if (!emailError.value.equals("Email exists") && newUser) {
            emailError.value = ""
            Log.d("myTagEmails", "New email is:$email")
            true
        } else if (!emailError.value.equals("User does not exist") && !newUser) {
            emailError.value = ""
            Log.d("myTagEmails", "New email is:$email")
            true
        } else if (emailError.equals("")) true
        else {
            emailError.value = "This email already exists"
            Log.d("myTagEmails", "email already exists")
            false
        }
    }

    private fun validatePassCon(): Boolean {
        val checkPassCon = passCon
        val checkPass = password
        return if (checkPassCon == checkPass) {
            Log.d("myT", "passwords match")
            passConError.value = ""
            true
        } else {
            passConError.value = "Passwords do not match"
            false
        }
    }

    fun setPassword(tPassword : String) {
        val checkError = LoginUtils.validatePassword(tPassword)
        if (checkError.isEmpty()) {
            password = tPassword
            passwordError.value = checkError
        } else passwordError.value = checkError
    }

    fun submitNewUser() {
        if (!validateEmail() || !validatePassCon()) return
        else addLoginDetails()
    }

    fun loginUser(email: String, pass: String) {
        setEmail(email)
        setPassword(pass)
        if (!validateEmail()) return
        else viewModelScope.launch(Dispatchers.IO) {
            try {
                progressBar.postValue(View.VISIBLE)
                firebaseRepository.signInUser(email, password).let { uid ->
                    userPreferences.updateLoginData(email, password, uid)
                    loginChannel.send(LoginEvents.LoggedInEvent)
                }
            } catch (e: FirebaseAuthException) {
                Log.d(TAG, "user not logged in: ${e.message}")
                loggedIn.postValue(false)
            }
        }
    }

    private fun addLoginDetails() {
        if (email.isNotEmpty() && password.isNotEmpty()) viewModelScope.launch(Dispatchers.IO) {
            try {
                progressBar.postValue(View.VISIBLE)
                firebaseRepository.createUser(email, password).let { uid ->
                    userPreferences.updateLoginData(email, password, uid)
                    loginChannel.send(LoginEvents.RegisteredEvent)
                }
            } catch (e: FirebaseAuthException) {
                Log.d(TAG, "user not created: ${e.message}")
                loggedIn.postValue(false)
            }
        }
    }

    fun setEmail(tEmail: String) { email = tEmail }
    fun setPassCon(tPassCon: String) { passCon = tPassCon }

    fun getLoggedIn(): LiveData<Boolean> { return loggedIn}
    fun getEmailError(): LiveData<String> { return emailError }
    fun getPasswordError(): LiveData<String> { return passwordError}
    fun getPassConError(): LiveData<String> { return passConError }
    fun getProgressBar(): LiveData<Int> { return progressBar }
//    fun getLoginError(): LiveData<String> { return loginError }

    sealed class LoginEvents {
        object UserNotFoundEvent: LoginEvents()
        object RegisteredEvent: LoginEvents()
        object LoggedInEvent: LoginEvents()
    }
}