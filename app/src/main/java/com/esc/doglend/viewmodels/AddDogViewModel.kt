package com.esc.doglend.viewmodels

import androidx.lifecycle.ViewModel
import com.esc.doglend.entities.Dog
import com.esc.doglend.repositories.ProfileFirebaseRepository
import com.esc.doglend.utils.login.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AddDogViewModel @Inject constructor(
    private val firebaseRepository: ProfileFirebaseRepository,
    userPreferences: UserPreferences
): ViewModel() {

    private val loginPref = userPreferences.loginPref
    private val addDogChannel = Channel<AddDogEvents>()
    val addDogEvent = addDogChannel.receiveAsFlow()

    suspend fun setDog(dog: Dog) {
        loginPref.collect { loginPref ->
            if (loginPref.uid.isEmpty()) {
                addDogChannel.send(AddDogEvents.UserNotFoundEvent)
            }
            else {
                if (firebaseRepository.addDog(dog, loginPref.uid))
                    addDogChannel.send(AddDogEvents.SubmitDog(dog))
                else addDogChannel.send(AddDogEvents.SaveDogError)
            }

        }
    }

    sealed class AddDogEvents {
        data class SubmitDog(val dog: Dog): AddDogEvents()
        object SaveDogError: AddDogEvents()
        object UserNotFoundEvent: AddDogEvents()
    }
}