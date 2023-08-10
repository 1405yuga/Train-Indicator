package com.example.trainindicator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trainindicator.databinding.FragmentAppGuideDialogListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppGuideDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAppGuideDialogListDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppGuideDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        BottomSheetBehavior.from(binding.bottomSheetLayout).state = BottomSheetBehavior.STATE_EXPANDED

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}