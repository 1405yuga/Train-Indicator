package com.example.trainindicator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        checkLocationPermission()

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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, you can proceed with location-related operations
                Log.d(TAG,"Location permission granted")
            } else {
                // Permission denied, handle this situation (e.g., show an explanation or disable location-related features)
                Toast.makeText(requireContext(),"If location permission note allowed nearest station won't be fetched",Toast.LENGTH_LONG).show()
                Log.d(TAG,"Location permission denied")
            }
        }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            // ask permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        }
        else{
            // TODO: get current location
            Log.d(TAG,"Has location permission")
        }
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
                    mMap.addMarker(
                        MarkerOptions().position(position)
                            .title(station.name)
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    viewModel.createMarkerIcon(doc.id,station.status,requireContext())!!
                                )
                            )
                    )

                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(position, 10.5f)
                    )
                }
            }
        })
    }

}