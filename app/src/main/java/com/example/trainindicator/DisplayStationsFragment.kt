package com.example.trainindicator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.trainindicator.constants.ProjectConstants
import com.example.trainindicator.databinding.FragmentDisplayStationsBinding
import com.example.trainindicator.datastore.PreferenceRailwayType
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
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

private const val TAG = "DisplayStationsFragment tag"

class DisplayStationsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentDisplayStationsBinding
    private lateinit var viewModel: StationViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var preferenceRailwayType: PreferenceRailwayType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(StationViewModel::class.java)
        binding = FragmentDisplayStationsBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.VISIBLE

        checkLocationPermission()
        mapInitializer(savedInstanceState)

        preferenceRailwayType = PreferenceRailwayType(requireContext())
        preferenceRailwayType.preferences.asLiveData()
            .observe(viewLifecycleOwner, Observer { preferenceRailway ->

                if (preferenceRailway == ProjectConstants.CENTRAL_RAILWAY) {
                    binding.topAppBar.menu.getItem(1).isChecked = true
                } else if (preferenceRailway == ProjectConstants.HARBOUR_RAILWAY) {
                    binding.topAppBar.menu.getItem(2).isChecked = true
                } else {
                    binding.topAppBar.menu.getItem(0).isChecked = true
                }
                val fullForm =
                    if (preferenceRailway == ProjectConstants.WESTERN_RAILWAY) resources.getString(R.string.western_railway)
                    else if (preferenceRailway == ProjectConstants.CENTRAL_RAILWAY) resources.getString(
                        R.string.central_railway
                    )
                    else resources.getString(R.string.harbour_railway)
                binding.topAppBar.subtitle = fullForm
                binding.navigationView.getHeaderView(0)
                    .findViewById<TextView>(R.id.railwayType).text =
                    fullForm
                FirestoreFunctions.getStations(
                    requireContext(),
                    preferenceRailway,
                    viewModel.updateStationList
                )
            })

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            binding.progressBar.visibility = View.VISIBLE
            var railway: String
            if (menuItem.itemId == R.id.central_railway || menuItem.itemId == R.id.western_railway || menuItem.itemId == R.id.harbour_railway) {
                if (menuItem.itemId == R.id.central_railway) {
                    railway = ProjectConstants.CENTRAL_RAILWAY
                } else if (menuItem.itemId == R.id.western_railway) {
                    railway = ProjectConstants.WESTERN_RAILWAY
                } else {
                    railway = ProjectConstants.HARBOUR_RAILWAY
                }
                lifecycleScope.launch {
                    preferenceRailwayType.saveLayoutPrefrence(
                        requireContext(),
                        railway
                    )
                }
                menuItem.isChecked = true
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }

        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.close()
            when (menuItem.itemId) {
                R.id.app_guide -> {
                    navigateToMenuFragment(DisplayStationsFragmentDirections.actionDisplayStationsFragmentToAppGuideDialogFragment())
                }

                R.id.help -> {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/1405yuga/Train-Indicator/blob/main/README.md")
                        )
                    )
                    true
                }

                R.id.exit_app -> {
                    requireActivity().finish()
                    true
                }

                else -> {
                    false
                }
            }

        }

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }
        viewModel.nearestStations.observe(viewLifecycleOwner, Observer { pairOfNearestStations ->

            binding.navigationView.getHeaderView(0).apply {
                findViewById<TextView>(R.id.nearest_fast_st).text =
                    pairOfNearestStations.second?.toObject(Station::class.java)?.name
                        ?: ProjectConstants.NOT_FOUND

                findViewById<TextView>(R.id.nearest_slow_st).text =
                    pairOfNearestStations.first?.toObject(Station::class.java)?.name
                        ?: ProjectConstants.NOT_FOUND
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

    private fun navigateToMenuFragment(action: NavDirections): Boolean {
        findNavController().navigate(action)
        return true
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
                    "If location permission not allowed nearest station won't be fetched",
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

    override fun onMapReady(mMap: GoogleMap) {
        Log.d(TAG, "onMapReady called ${mMap}")
        binding.progressBar.visibility = View.VISIBLE

        viewModel.userLocation.observe(viewLifecycleOwner, Observer {
            if (viewModel.stationsList.value != null) {
                viewModel.nearestStations.value =
                    MapFunctions.getNearestStation(it, viewModel.stationsList.value!!)
            }
            MapFunctions.getLocality(it, requireContext(), viewModel.setUserLocality)
            addUserLocationMarker(mMap)
        })
        viewModel.userLocality.observe(viewLifecycleOwner, Observer {
            binding.navigationView.getHeaderView(0)
                .findViewById<TextView>(R.id.user_location).text = it
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
                    addStationMarker(mMap, doc)
                }
            }
            binding.progressBar.visibility = View.GONE
        })

        binding.navigationView.getHeaderView(0).apply {
            findViewById<LinearLayout>(R.id.user_location_layout)
                .setOnClickListener {
                    addUserLocationMarker(mMap, 12.0f)
                }

            findViewById<LinearLayout>(R.id.nearest_slow_st_layout)
                .setOnClickListener {
                    val stationDoc = viewModel.nearestStations.value?.first
                    addStationMarker(mMap, stationDoc, 12.0f)
                }
            findViewById<LinearLayout>(R.id.nearest_fast_st_layout)
                .setOnClickListener {
                    val stationDoc = viewModel.nearestStations.value?.second
                    addStationMarker(mMap, stationDoc, 12.0f)
                }
        }

    }

    private fun addStationMarker(
        mMap: GoogleMap,
        stationDoc: DocumentSnapshot?,
        zoomLevel: Float = 9.0f
    ) {
        val station = stationDoc?.toObject(Station::class.java)
        binding.drawerLayout.close()
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
                                stationDoc.id,
                                station.status,
                                requireContext()
                            )!!
                        )
                    )
            )?.showInfoWindow()
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(position, zoomLevel)
            )
        } else {
            Toast.makeText(requireContext(), "Location permission not granted!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addUserLocationMarker(mMap: GoogleMap, zoomLevel: Float = 9.0f) {
        binding.drawerLayout.close()
        if (viewModel.userLocation.value != null) {
            mMap.addMarker(
                MarkerOptions().position(viewModel.userLocation.value!!).title("You are here!")
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            viewModel.createMarkerIcon(null, null, requireContext())!!
                        )
                    )
            )?.showInfoWindow()
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(viewModel.userLocation.value!!, zoomLevel)
            )
        } else {
            Toast.makeText(requireContext(), "Location permission not granted!", Toast.LENGTH_SHORT)
                .show()
        }
    }

}