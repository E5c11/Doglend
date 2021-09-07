package com.esc.doglend.ui.fragments.profiles

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import com.esc.doglend.R
import com.esc.doglend.databinding.ProfileFragmentBinding
import com.esc.doglend.entities.Dog
import com.esc.doglend.utils.DogListAdapter
import com.esc.doglend.utils.exhaustive
import com.esc.doglend.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.profile_fragment), DogListAdapter.OnItemClickedListener {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ProfileFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfileFragmentBinding.bind(view)
        loadingDogs(View.VISIBLE)
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        viewModel.getDogs().observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.isEmpty()) binding.addDog.root.visibility = View.VISIBLE
                else setDogRecycler(it)
                loadingDogs(View.GONE)
            }

        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.profileEvent.collect{ event ->
                when (event) {
                    is ProfileViewModel.ProfileEvents.DogProfileEvent -> {
                        val action = ProfileFragmentDirections.actionProfileFragmentToDogProfileFragment(event.dog)
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    private fun setListeners() {
        onBackPressed()
        binding.addDog.root.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddDogFragment())
        }
    }

    private fun loadingDogs(vis: Int) {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fast)
        if (vis == View.VISIBLE) binding.registerProgress.animation = an
        else binding.registerProgress.clearAnimation()
        binding.registerProgress.visibility = vis
    }

    private fun setDogRecycler(dogs: List<Dog>) {
        GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
            .apply {
                binding.profileRecycler.layoutManager = this
        }
        binding.profileRecycler.adapter = DogListAdapter(dogs, this)
    }

    override fun onItemClick(dog: Dog) {
        viewModel.openDogProfile(dog)
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
                }
            })
    }
}