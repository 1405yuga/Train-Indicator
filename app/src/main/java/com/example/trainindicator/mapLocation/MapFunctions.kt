package com.example.trainindicator.mapLocation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.trainindicator.constants.ProjectConstants
import com.example.trainindicator.model.Station
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot

private const val TAG = "MapFunctions tag"

object MapFunctions {

    fun getLocality(latLng: LatLng, context: Context, setLocality: (String) -> (Unit)) {

        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1,
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val locality = addresses.get(0).locality
                        Log.d(TAG, "GeoCode assigned ${locality}")
                        setLocality(locality)
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        Toast.makeText(
                            context,
                            "Unable to get your current locality",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "GeoCode error ${errorMessage}")

                    }

                })
        } else {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                Log.d(
                    TAG,
                    "GeoCode - else loop ${addresses?.get(0)?.locality}"
                )
                val locality = addresses?.get(0)?.locality.toString()
                setLocality(locality)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to get your current locality", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "GeoCode error - else loop ${e.message}")
            }
        }
    }

    private fun haversineDistanceCalculation(userLocation: LatLng, station: LatLng): Double {
        val lat1 = userLocation.latitude
        val lat2 = station.latitude
        val lon1 = userLocation.longitude
        val lon2 = station.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return ProjectConstants.EARTH_RADIUS_IN_KM * c
    }

    fun getNearestStation(
        userLocation: LatLng,
        stationsList: List<DocumentSnapshot>
    ): Pair<DocumentSnapshot?, DocumentSnapshot?> {
        var nearestSlowStation: DocumentSnapshot? = null
        var nearestFastStation: DocumentSnapshot? = null

        var minDistanceToSlowStation = Double.MAX_VALUE
        var minDistanceToFastStation = Double.MAX_VALUE

        for (stationSnapshot in stationsList) {
            val station = stationSnapshot.toObject(Station::class.java)
            if (station != null && station.coordinates != null) {
                val stationLatLng =
                    LatLng(station.coordinates.latitude, station.coordinates.longitude)
                val distance = haversineDistanceCalculation(userLocation, stationLatLng)

                if (station.status == ProjectConstants.SLOW && distance < minDistanceToSlowStation) {
                    minDistanceToSlowStation = distance
                    nearestSlowStation = stationSnapshot
                }

                if (station.status == ProjectConstants.FAST && distance < minDistanceToFastStation) {
                    minDistanceToFastStation = distance
                    nearestFastStation = stationSnapshot
                }
            }
        }
        return Pair(nearestSlowStation, nearestFastStation)
    }

}