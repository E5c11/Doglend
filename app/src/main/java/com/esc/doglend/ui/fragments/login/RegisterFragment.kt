package com.esc.doglend.ui.fragments.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esc.doglend.R
import com.esc.doglend.databinding.RegisterFragmentBinding
import com.esc.doglend.viewmodels.RegisterViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.register_fragment), OnMapReadyCallback {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var map: GoogleMap
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var mapIcon: MarkerOptions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterFragmentBinding.bind(view)
        binding.regMap.onCreate(savedInstanceState)
        binding.regMap.getMapAsync(this)
        Log.d("myT", "onViewCreated: ")
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.getNameError().observe(viewLifecycleOwner, { binding.regName.error = it })
        viewModel.getSurnameError().observe(viewLifecycleOwner, { binding.regSurname.error = it })
        viewModel.getPhoneError().observe(viewLifecycleOwner, { binding.regPhone.error = it })
        viewModel.getPositionError().observe(viewLifecycleOwner, { binding.regName.error = it })
        viewModel.getLocAddress().observe(viewLifecycleOwner, {
            binding.mapSearch.requestFocus()
            binding.mapSearch.clearFocus()
            binding.mapSearch.setText(it, false)
        })
        viewModel.getRegisteredError().observe(viewLifecycleOwner, {
            binding.posErrorText.text = it
            binding.posErrorText.visibility = View.VISIBLE
            setProgressBar(View.GONE)
        })
        viewModel.getRegistered().observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToProfileFragment())
            } else {
                binding.posErrorText.visibility = View.VISIBLE
                binding.posErrorText.text = "Details not uploaded, try again"
            }
            setProgressBar(View.GONE)
        })
    }

    private fun setListeners() {
        binding.regNameInput.setOnFocusChangeListener { _, focus ->
            if (!focus) {
                Log.d("myT", "setListeners: name")
                viewModel.setName(binding.regName.editText?.text.toString().trim {it <= ' '})
            }
        }
        binding.regSurnameInput.setOnFocusChangeListener { _, focus ->
            if (!focus)
                viewModel.setSurname(binding.regSurname.editText?.text.toString().trim {it <= ' '})
        }
        binding.regPhoneInput.setOnFocusChangeListener { _, focus ->
            if (!focus) viewModel.setPhone(binding.regPhone.editText?.text?.filter
                {!it.isWhitespace()}.toString())
        }
        binding.mapSearch.setOnClickListener {
//            binding.mapSearch.isIconified = false
        }
        binding.mapSearch.setOnFocusChangeListener { _, focus ->
            if (focus) binding.regScroll.visibility = View.GONE
            else binding.regScroll.visibility = View.VISIBLE
        }
        binding.submit.setOnClickListener {
            setProgressBar(View.VISIBLE)
            viewModel.setPosition(mapIcon.position)
            viewModel.setName(binding.regName.editText?.text.toString().trim {it <= ' '})
            viewModel.setSurname(binding.regSurname.editText?.text.toString().trim {it <= ' '})
            viewModel.setPhone(binding.regPhone.editText?.text?.filter {!it.isWhitespace()}.toString())
            viewModel.submitNewUser() }
    }

    private fun setProgressBar(vis: Int) {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fast)
        binding.registerProgress.animation = an
        binding.registerProgress.visibility = vis
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        map.setOnMapLongClickListener {
            map.clear()
            mapIcon = MarkerOptions().position(it).title("My Location")
            map.addMarker(mapIcon)
            viewModel.setPosition(mapIcon.position)
        }
        moveLocationIcon()
    }

    private fun moveLocationIcon() {
        val locationButton= (binding.regMap.findViewById<View>(Integer.parseInt("1"))
            .parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30);
    }

    override fun onResume() {
        super.onResume()
        binding.regMap.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.regMap.onPause()
    }
    override fun onStart() {
        super.onStart()
        binding.regMap.onStart()
    }
    override fun onStop() {
        super.onStop()
        binding.regMap.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        binding.regMap.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.regMap.onSaveInstanceState(outState)
    }

}