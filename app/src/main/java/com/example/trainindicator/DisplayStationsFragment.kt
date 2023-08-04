package com.example.trainindicator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trainindicator.databinding.FragmentDisplayStationsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "DisplayStationsFragment tag"

class DisplayStationsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDisplayStationsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST) {
            Log.d(TAG, "Map initializer $it")
        }

        binding = FragmentDisplayStationsBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        val mMap = p0

        // Add a marker in Sydney and move the camera

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}