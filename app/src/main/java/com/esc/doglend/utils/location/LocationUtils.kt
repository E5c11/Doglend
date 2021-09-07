package com.esc.doglend.utils.location

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

object LocationUtils {

    private val locationRequest = LocationRequest.create().apply {
        interval = 500
        fastestInterval = 100
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private lateinit var currentLocation: LatLng

    @SuppressLint("MissingPermission")
    fun sendLocationRequest(application: Application): LatLng {
        LocationServices.getFusedLocationProviderClient(application)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) { locationResult ?: return
                        if (locationResult.locations.isNotEmpty()) {
                            val latestLocationIndex = locationResult.locations.size - 1
                            currentLocation = LatLng(locationResult.locations[latestLocationIndex].latitude,
                                locationResult.locations[latestLocationIndex].longitude)
    //                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12f))
                            LocationServices.getFusedLocationProviderClient(application)
                                .removeLocationUpdates(this)
                        }
                    }
        }, Looper.getMainLooper())
        return currentLocation
    }
}