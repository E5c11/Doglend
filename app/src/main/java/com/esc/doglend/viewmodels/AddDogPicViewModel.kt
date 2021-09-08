package com.esc.doglend.viewmodels

import androidx.lifecycle.ViewModel
import com.esc.doglend.repositories.ProfileFirebaseRepository
import com.esc.doglend.utils.login.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AddDogPicViewModel @Inject constructor(
    private val firebaseRepository: ProfileFirebaseRepository,
    userPreferences: UserPreferences
): ViewModel() {

    private val loginPref = userPreferences.loginPref
    private val addDogChannel = Channel<AddDogViewModel.AddDogEvents>()
    val addDogEvent = addDogChannel.receiveAsFlow()


}