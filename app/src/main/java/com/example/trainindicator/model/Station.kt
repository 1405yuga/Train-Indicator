package com.example.trainindicator.model

import com.google.firebase.firestore.GeoPoint

data class Station(
    val name: String? = null,
    val status: String? = null,
    val coordinates: GeoPoint? = null
)
