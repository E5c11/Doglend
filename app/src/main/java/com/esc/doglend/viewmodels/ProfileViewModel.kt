package com.esc.doglend.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.esc.doglend.entities.Dog
import com.esc.doglend.repositories.DL_OWNERS
import com.esc.doglend.repositories.DL_OWNER_DOGS
import com.esc.doglend.repositories.ProfileFirebaseRepository
import com.esc.doglend.utils.FirestoreQueryLiveData
import com.esc.doglend.utils.login.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepository: ProfileFirebaseRepository,
    userPreferences: UserPreferences
) : ViewModel() {

    private val loginPref = userPreferences.loginPref
    private val dogs = MutableLiveData<List<Dog>>()
    private val profileChannel = Channel<ProfileEvents>()
    val profileEvent = profileChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            loginPref.collect { dogs.postValue(firebaseRepository.fetchDogs(it.uid)) }
        }
    }

    fun openDogProfile(dog: Dog) {
        viewModelScope.launch {
            profileChannel.send(ProfileEvents.DogProfileEvent(dog))
        }
    }

    sealed class ProfileEvents {
        data class DogProfileEvent(val dog: Dog): ProfileEvents()
    }

    fun getDogs(): LiveData<List<Dog>> { return dogs }
}