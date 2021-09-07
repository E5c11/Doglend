package com.esc.doglend.dagger

import android.content.Context
import android.location.Geocoder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFireAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context) = Geocoder(context, Locale.getDefault())

//    @Provides
//    @Singleton
//    fun provideNumberValidation(@ApplicationContext context: Context) = PhoneNumberUtil.createInstance(context)

}