package com.example.trainindicator.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trainindicator.R
import com.example.trainindicator.constants.ProjectConstants
import com.google.firebase.firestore.DocumentSnapshot

class StationViewModel : ViewModel() {

    private var _stationsList = MutableLiveData(listOf<DocumentSnapshot>())
    val stationsList: LiveData<List<DocumentSnapshot>> = _stationsList

    val updateStationList: (List<DocumentSnapshot>) -> (Unit) = {
        _stationsList.value = it
    }

    fun createMarkerIcon(status: String?, context: Context): Bitmap? {
        val height = 120
        val width = 80
        var image = R.drawable.marker_yellow
        if(status == ProjectConstants.FAST) image = R.drawable.marker_green
        val bitmap = BitmapFactory.decodeResource(context.resources, image)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}