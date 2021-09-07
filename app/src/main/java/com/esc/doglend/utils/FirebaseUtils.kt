package com.esc.doglend.utils

import android.util.Log
import androidx.lifecycle.LiveData
import com.esc.doglend.entities.Dog
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object FirebaseUtils {

    fun queryToList(querySnapshot: QuerySnapshot): List<Dog> {
        if (querySnapshot.documents.isEmpty()) Log.d("myT", "there are no dogs: ")
        return querySnapshot.documents.map { dog ->
            dog.toObject(Dog::class.java)!!
        }
    }

}

class FirestoreQueryLiveData(
    private val query: Query
): LiveData<QuerySnapshot>(), EventListener<QuerySnapshot> {
    private lateinit var registration: ListenerRegistration

    override fun onActive() {
        super.onActive()
        registration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        registration.remove()
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        postValue(snapshot!!)
    }
}