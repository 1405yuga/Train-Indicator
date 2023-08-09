package com.example.trainindicator.mapLocation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng

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
}