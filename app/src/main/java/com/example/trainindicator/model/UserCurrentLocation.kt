package com.example.trainindicator.model

import com.google.android.gms.maps.model.LatLng

data class UserCurrentLocation(
    val coordinates: LatLng,
    val fastStation: Station,
    val slowStation: Station
)
