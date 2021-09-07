package com.esc.doglend.repositories

import com.esc.doglend.utils.FirebaseUtils
import com.esc.doglend.utils.login.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

const val DL_OWNERS = "owners"

@Singleton
class LoginFirebaseRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore) {

    suspend fun authenticate(email: String): Boolean =
        firebaseAuth.fetchSignInMethodsForEmail(email).await()
            .signInMethods.orEmpty().isEmpty()


    suspend fun createUser(email: String, password: String): String {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return firebaseAuth.currentUser?.uid ?: throw FirebaseAuthException("", "")
    }

    suspend fun signInUser(email: String, password: String): String {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return firebaseAuth.currentUser?.uid ?: throw FirebaseAuthException("", "")
    }

    suspend fun setUserDetails(user: UserDetails, uid: String): Boolean {
        return try {
            firestore.collection(DL_OWNERS).document(uid).set(user).await()
            true
        } catch (exception: Exception) {
            // Handle exception
            false
        }
    }

}