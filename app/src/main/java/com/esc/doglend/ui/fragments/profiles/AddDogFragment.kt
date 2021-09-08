package com.esc.doglend.ui.fragments.profiles

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.esc.doglend.R
import com.esc.doglend.databinding.AddDogFragemntBinding
import com.esc.doglend.entities.*
import com.esc.doglend.utils.*
import com.esc.doglend.viewmodels.AddDogViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddDogFragment: Fragment(R.layout.add_dog_fragemnt) {

    private lateinit var binding: AddDogFragemntBinding
    private val viewModel: AddDogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddDogFragemntBinding.bind(view)
        setBreed()
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.submit.setOnClickListener {
            setProgressBar(View.VISIBLE)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewModel.setDog(
                    Dog(
                        name = binding.nameDogEdit.editText?.text.toString(),
                        breed = binding.breedAuto.text.toString(),
                        sex = Sex.valueOf(binding.sex.selectedItem.toString()),
                        age = binding.age.value,
                        size = Size.valueOf(Size::class.enumValues()[binding.size.value]),
                        sociability = Sociability.valueOf(Sociability::class.enumValues()[binding.sociability.value]),
                        energy = Energy.valueOf(Energy::class.enumValues()[binding.energy.value]),
                        stamina = binding.stamina.value,
                        stamina_unit = StaminaUnit.valueOf(binding.staminaUnits.selectedItem.toString())
                    )
                )
            }
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addDogEvent.collect { event ->
                when (event) {
                    is AddDogViewModel.AddDogEvents.SubmitDog -> {
//                        findNavController().navigate(AddDogFragmentDirections.actionAddDogFragmentToAddDogPicFragment())
                        findNavController().navigate(AddDogFragmentDirections.actionAddDogFragmentToDogProfileFragment(event.dog))
                        setProgressBar(View.GONE)
                    } is AddDogViewModel.AddDogEvents.SaveDogError -> {
                        setProgressBar(View.GONE)
                        Snackbar.make(requireView(), "User not found", Snackbar.LENGTH_LONG).show()
                    } is AddDogViewModel.AddDogEvents.UserNotFoundEvent -> {
                        findNavController().navigate(AddDogFragmentDirections.actionAddDogFragmentToLoginFragment())
                        setProgressBar(View.GONE)
                    }
                }.exhaustive
            }
        }
    }

    private fun setProgressBar(vis: Int) {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fast)
        if (vis == View.VISIBLE) binding.progressBar.animation = an
        else binding.progressBar.clearAnimation()
        binding.progressBar.visibility = vis
    }

    private fun setBreed() {
        val breeds = resources.getStringArray(R.array.breeds)
        val breedAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breeds)
        binding.breedAuto.threshold = 1
        binding.breedAuto.setOnItemClickListener { parent, view, position, id -> hideKeyboard() }
        binding.breedAuto.setAdapter(breedAdapter)
        setSex()
    }

    private fun setSex() {
        val sexAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, Sex::class.enumValues())
        binding.sex.adapter = sexAdapter
        setAge()
    }

    private fun setAge() {
        binding.age.apply { minValue = 1; maxValue = 15; value = 5 }
        setSize()
    }

    private fun setSize() {
        binding.size.apply {
            minValue = 0; maxValue = 3; value = 1
            displayedValues = Size::class.enumValues()
        }
        setSociability()
    }

    private fun setSociability() {
        binding.sociability.apply {
            minValue = 0; maxValue = 4; value = 2
            displayedValues = Sociability::class.enumValues()
        }
        setEnergy()
    }

    private fun setEnergy() {
        binding.energy.apply {
            minValue = 0; maxValue = 4; value = 2
            displayedValues = Energy::class.enumValues()
        }
        setStamina()
    }

    private fun setStamina() {
        binding.stamina.apply { minValue = 1; maxValue = 10; value = 5 }
        val staminaAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, StaminaUnit::class.enumValues())
        binding.staminaUnits.adapter = staminaAdapter
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}