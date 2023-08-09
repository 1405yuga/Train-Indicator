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

    fun getLocality(latLng: LatLng, context: Context): String {

        val geocoder = Geocoder(context)
        var locality = "Not found"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1,
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        Log.d(TAG, "GeoCode ${addresses.get(0).locality}")
                        locality = addresses.get(0).locality
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
                locality = addresses?.get(0)?.locality.toString()
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to get your current locality", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "GeoCode error - else loop ${e.message}")
            }
        }
        return locality
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

    fun getNearestFastStation(
        userLocation: LatLng,
        stationsList: List<DocumentSnapshot>
    ): DocumentSnapshot {
        var nearestStation = stationsList.get(0)
        var minDistance = haversineDistanceCalculation(
            userLocation, LatLng(
                nearestStation.toObject(Station::class.java)?.coordinates!!.latitude,
                nearestStation.toObject(Station::class.java)?.coordinates!!.longitude
            )
        )

        for (station in stationsList) {
            val temp = haversineDistanceCalculation(
                userLocation, LatLng(
                    station.toObject(Station::class.java)?.coordinates!!.latitude,
                    station.toObject(Station::class.java)?.coordinates!!.longitude
                )
            )
            if (temp < minDistance) {
                minDistance = temp
                nearestStation = station
            }
        }
        return nearestStation
    }

}