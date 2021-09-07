package com.esc.doglend.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esc.doglend.R
import com.esc.doglend.databinding.HomeFragmentBinding
import com.esc.doglend.utils.PermissionsCheck
import com.esc.doglend.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var _binding : HomeFragmentBinding
    private val requestCode = 1;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeFragmentBinding.bind(view)
        requestLocation()
        setButtonOnClick()
        //setHasOptionsMenu(true)
    }

    private fun setButtonOnClick() {
        _binding.findDogBt.setOnClickListener {
            if (PermissionsCheck.hasLocationPermission(requireContext())) {
            } else askPermission()
        }
        _binding.loginBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
        _binding.reviewBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddDogPicFragment())
        }
    }

    private fun requestLocation() {
        if (PermissionsCheck.hasLocationPermission(requireContext())) return
        else askPermission()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            this.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && !PermissionsCheck.hasLocationPermission(requireContext())) {
                    askPermission()
                }
                return
            }
        }
    }
    private fun askPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
    }
}