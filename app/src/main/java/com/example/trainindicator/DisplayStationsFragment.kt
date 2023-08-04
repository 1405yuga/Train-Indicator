package com.example.trainindicator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trainindicator.databinding.FragmentDisplayStationsBinding

class DisplayStationsFragment : Fragment() {

    private lateinit var binding: FragmentDisplayStationsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDisplayStationsBinding.inflate(inflater,container,false)
        return binding.root
    }

}