package com.example.trainindicator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.trainindicator.constants.ProjectConstants
import com.example.trainindicator.databinding.FragmentDisplayStationsBinding
import com.example.trainindicator.firebase.FirestoreFunctions
import com.example.trainindicator.mapLocation.MapFunctions
import com.example.trainindicator.model.Station
import com.example.trainindicator.model.StationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(StationViewModel::class.java)
        binding = FragmentDisplayStationsBinding.inflate(inflater, container, false)

        checkLocationPermission()
        mapInitializer(savedInstanceState)

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.central_railway || menuItem.itemId == R.id.western_railway) {
                if (menuItem.itemId == R.id.central_railway) {
                    viewModel.setRailwayType(ProjectConstants.CENTRAL_RAILWAY)
                } else {
                    viewModel.setRailwayType(ProjectConstants.WESTERN_RAILWAY)
                }
                menuItem.isChecked = true
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }

        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

        when(menuItem.itemId){
            R.id.platform_guide->{
                navigateToMenuFragment(DisplayStationsFragmentDirections.actionDisplayStationsFragmentToPlatformGuideFragment())
                binding.drawerLayout.close()
                true
            }
            R.id.app_guide ->{
                navigateToMenuFragment(DisplayStationsFragmentDirections.actionDisplayStationsFragmentToAppGuideDialogFragment())
                binding.drawerLayout.close()
                true
            }
            else ->{
                false
            }
        }

        }

        viewModel.railwayType.observe(viewLifecycleOwner, Observer {
            val fullForm =
                if (it == ProjectConstants.WESTERN_RAILWAY) resources.getString(R.string.western_railway)
                else resources.getString(R.string.central_railway)
            binding.topAppBar.subtitle = fullForm
            binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.railwayType).text =
                fullForm
            FirestoreFunctions.getStations(requireContext(), it, viewModel.updateStationList)
        })

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
        viewModel.nearestStations.observe(viewLifecycleOwner, Observer { pairOfNearestStations ->

            binding.navigationView.getHeaderView(0).apply {
                findViewById<TextView>(R.id.nearest_fast_st).text =
                    pairOfNearestStations.second?.toObject(Station::class.java)?.name ?: "Not found"

                findViewById<TextView>(R.id.nearest_slow_st).text =
                    pairOfNearestStations.first?.toObject(Station::class.java)?.name ?: "Not found"
            }

        })

        return binding.root
    }

    private fun mapInitializer(savedInstanceState: Bundle?) {
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST) {
            Log.d(TAG, "Map initializer $it")
        }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    private fun navigateToMenuFragment(action:NavDirections){
        findNavController().navigate(action)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, you can proceed with location-related operations
                Log.d(TAG, "Location permission granted")
                checkLocationPermission()
            } else {
                // Permission denied, handle this situation (e.g., show an explanation or disable location-related features)
                Toast.makeText(
                    requireContext(),
                    "If location permission note allowed nearest station won't be fetched",
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG, "Location permission denied")
            }
        }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Has location permission")

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                Log.d(TAG, "fusedLocationProviderClient $it")
                viewModel.setUserLocation(LatLng(it.latitude, it.longitude))
            }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Unable to fetch current location",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Unable to fetch location ${it.message}")
                }

        } else {
            // ask permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        val mMap = p0
        Log.d(TAG, "onMapReady called ${mMap}")

        viewModel.userLocation.observe(viewLifecycleOwner, Observer {
            if (viewModel.stationsList.value != null) {
                viewModel.nearestStations.value =
                    MapFunctions.getNearestStation(it, viewModel.stationsList.value!!)
            }

            binding.navigationView.getHeaderView(0)
                .findViewById<TextView>(R.id.user_location).text =
                MapFunctions.getLocality(it, requireContext())
            mMap.addMarker(
                MarkerOptions().position(it).title("You are here!").icon(
                    BitmapDescriptorFactory.fromBitmap(
                        viewModel.createMarkerIcon(null, null, requireContext())!!
                    )
                )
            )?.showInfoWindow()
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(it, 9.0f)
            )
        })

        viewModel.stationsList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "viewmodel data : ${it.size}")
            if (viewModel.userLocation.value != null) {
                viewModel.nearestStations.value =
                    MapFunctions.getNearestStation(viewModel.userLocation.value!!, it)
            }
            mMap.clear()
            checkLocationPermission()
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
                                    viewModel.createMarkerIcon(
                                        doc.id,
                                        station.status,
                                        requireContext()
                                    )!!
                                )
                            )
                    )

                }
            }
        })


    }

}