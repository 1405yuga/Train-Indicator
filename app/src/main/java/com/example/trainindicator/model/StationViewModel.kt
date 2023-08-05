package com.example.trainindicator.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot

class StationViewModel : ViewModel() {

    private var _stationsList = MutableLiveData(listOf<DocumentSnapshot>())
    val stationsList: LiveData<List<DocumentSnapshot>> = _stationsList

    private val updateStationList: (List<DocumentSnapshot>) -> (Unit) = {
        _stationsList.value = it
    }
}