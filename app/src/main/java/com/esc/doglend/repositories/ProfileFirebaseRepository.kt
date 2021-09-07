package com.esc.doglend.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.esc.doglend.entities.Dog
import com.esc.doglend.utils.FirebaseUtils
import com.esc.doglend.utils.FirestoreQueryLiveData
import com.esc.doglend.utils.calendar.data.Available
import com.esc.doglend.utils.calendar.data.PresetAvailability
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val DL_OWNER_DOGS = "dogs"
const val DOG_AVAILABILITY = "availability"
const val PRESET_AVAILABILITY = "preset"

@Singleton
class ProfileFirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchDogs(uid: String): List<Dog>{
        val dogCall = firestore.collection(DL_OWNERS).document(uid)
            .collection(DL_OWNER_DOGS).get().await()
        return if (!dogCall.isEmpty) FirebaseUtils.queryToList(dogCall)
        else listOf()

    }

    fun getOwnerDogsFromFirestore(uid: String): LiveData<List<Dog>> {
        val firestoreRef = firestore.collection(DL_OWNERS).document(uid).collection(DL_OWNER_DOGS)
        val queryData = FirestoreQueryLiveData(firestoreRef)
        val dogList: List<Dog> = arrayListOf()
        Log.d("myT", "getOwnerDogsFromFirestore: $uid")
        return Transformations.map(queryData) { queryList ->
            Log.d("myT", "checking dogs: ")
            dogList.toMutableList().clear()
            if (queryList.documents.isEmpty()) Log.d("myT", "there are no dogs: ")
            for (doc in queryList.documents) {
                val dogSnap = doc.toObject(Dog::class.java)
                dogList.toMutableList().add(dogSnap!!)
            }
            dogList
        }
    }

    suspend fun addDog(dog: Dog, uid: String): Boolean {
        return try {
            firestore.collection(DL_OWNERS).document(uid)
                .collection(DL_OWNER_DOGS).document(dog.name).set(dog).await()
            true
        } catch (exception: Exception) {
            //Handle exception
            false
        }
    }

    suspend fun updateAvailability(mutableList: List<Available>, uid: String): Boolean {
        return try {
            firestore.collection(DL_OWNERS).document(uid).collection(DOG_AVAILABILITY)
                .document(PRESET_AVAILABILITY).set(mutableList).await()
            true
        } catch (exception: Exception) {
            //Handle exception
            false
        }
    }

    suspend fun fetchAvailability(uid: String): PresetAvailability?{
        return try {
            firestore.collection(DL_OWNERS).document(uid).collection(DOG_AVAILABILITY)
                .document(PRESET_AVAILABILITY).get().await().toObject(PresetAvailability::class.java)
        } catch (exception: Exception) {
            //Handle exception
            null
        }

    }
}