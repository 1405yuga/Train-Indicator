package com.example.trainindicator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.trainindicator.constants.ProjectConstants
import com.example.trainindicator.databinding.FragmentPlatformGuideBinding
import com.example.trainindicator.firebase.FirestoreFunctions
import com.example.trainindicator.model.StationViewModel

private const val TAG = "PlatformGuideFragment tag"

class PlatformGuideFragment : Fragment() {

    private lateinit var binding: FragmentPlatformGuideBinding
    private lateinit var viewModel: StationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(StationViewModel::class.java)
        binding = FragmentPlatformGuideBinding.inflate(inflater, container, false)

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

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(PlatformGuideFragmentDirections.actionPlatformGuideFragmentToDisplayStationsFragment())
        }

        viewModel.railwayType.observe(viewLifecycleOwner, Observer {
            binding.topAppBar.subtitle =
                if (it == ProjectConstants.WESTERN_RAILWAY) resources.getString(R.string.western_railway)
                else resources.getString(R.string.central_railway)
            FirestoreFunctions.getStations(requireContext(), it, viewModel.updateStationList)
        })
        return binding.root
    }

}