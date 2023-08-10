package com.example.trainindicator.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.trainindicator.constants.ProjectConstants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "FirestoreFunctions tag"

object FirestoreFunctions {

    fun getStations(
        context: Context, railwayType: String,
        updateStationList: (List<DocumentSnapshot>) -> (Unit)
    ) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(railwayType)
            .get()
            .addOnSuccessListener {
                updateStationList(it.documents)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to get stations", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "getStations error : ${it.message}")
            }
    }

    fun getRoutes(context: Context,railwayType: String,stationCode : String){
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(railwayType)
            .document(stationCode)
            .collection(ProjectConstants.ROUTES)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "routes are : ${it.documents}")
                for(doc in it.documents){
                    Log.d(TAG,"${doc.get(ProjectConstants.FAST)}")
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to get routes", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "getStations error : ${it.message}")
            }
    }
}