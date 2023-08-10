package com.example.trainindicator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.trainindicator.constants.ProjectConstants
import com.example.trainindicator.databinding.FragmentPlatformGuideBinding
import com.example.trainindicator.model.StationViewModel

private const val TAG = "PlatformGuideFragment tag"

class PlatformGuideFragment : Fragment() {

    private lateinit var binding : FragmentPlatformGuideBinding
    private lateinit var viewModel : StationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(StationViewModel::class.java)
        binding = FragmentPlatformGuideBinding.inflate(inflater,container,false)

        return binding.root
    }

}