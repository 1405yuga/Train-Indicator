package com.example.trainindicator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.trainindicator.constants.ProjectConstants
import com.example.trainindicator.databinding.FragmentDisplayStationsBinding
import com.example.trainindicator.firebase.FirestoreFunctions
import com.example.trainindicator.model.Station
import com.example.trainindicator.model.StationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "DisplayStationsFragment tag"

class DisplayStationsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDisplayStationsBinding
    private lateinit var viewModel: StationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(StationViewModel::class.java)
        binding = FragmentDisplayStationsBinding.inflate(inflater, container, false)
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST) {
            Log.d(TAG, "Map initializer $it")
        }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        FirestoreFunctions.getStations(requireContext(), viewModel.updateStationList)
        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        val mMap = p0

        viewModel.stationsList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "viewmodel data : ${it.size}")
            for (doc in it) {
                val station = doc.toObject(Station::class.java)
                if (station != null) {
                    val position = LatLng(
                        station.coordinates!!.latitude,
                        station.coordinates.longitude
                    )
                    if(station.status == ProjectConstants.FAST){
                        mMap.addMarker(
                            MarkerOptions().position(position)
                                .title(station.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(140F))
                        )
                    }
                    else{
                        mMap.addMarker(
                            MarkerOptions().position(position)
                                .title(station.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(50F))
                        )
                    }

                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(position, 10.5f)
                    )
                }
            }
        })
    }

}