package com.esc.doglend.ui.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.esc.doglend.R
import com.esc.doglend.databinding.LoginFragmentBinding
import com.esc.doglend.utils.LetterWatcher
import com.esc.doglend.utils.exhaustive
import com.esc.doglend.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "myT"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding : LoginFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        setLoginListeners()
        setLoginObservers()
    }

    private fun setLoginObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvents.UserNotFoundEvent -> {
                        setProgressBar(View.GONE)
                        Log.d(TAG, "user not found: ")
                        Snackbar.make(requireView(), "User not found", Snackbar.LENGTH_LONG).show()
                    } is LoginViewModel.LoginEvents.RegisteredEvent -> {
                        Log.d(TAG, "go to register: ")
                        findNavController()
                            .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
                    } is LoginViewModel.LoginEvents.LoggedInEvent -> {
                        findNavController()
                            .navigate(LoginFragmentDirections.actionLoginFragmentToProfileFragment())
                    }
                }.exhaustive
            }
        }
        viewModel.getEmailError().observe(viewLifecycleOwner, { binding.regEmail.error = it })
        viewModel.getPasswordError().observe(viewLifecycleOwner, { binding.regPassword.error = it })
        viewModel.getProgressBar().observe(viewLifecycleOwner, { setProgressBar(it) })
        viewModel.getLoggedIn().observe(viewLifecycleOwner, {binding.registerProgress.visibility = View.GONE})
    }

    private fun setRegisterObservers() {
        viewModel.getPassConError().observe(viewLifecycleOwner, {binding.regPasswordInputConEdit.error = it})
    }

    private fun setProgressBar(vis: Int) {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fast)
        if (vis == View.VISIBLE) binding.registerProgress.animation = an
        else binding.registerProgress.clearAnimation()
        binding.registerProgress.visibility = vis
    }

    private fun enableRegister(vis: Int, btnText: String) {
        binding.regPasswordCon.visibility = vis
        binding.submit.text = btnText
    }

    private fun setLoginListeners() {
        binding.registerText.setOnClickListener {
            if (binding.registerText.text.equals(resources.getString(R.string.register))) {
                viewModel.setNewUser(true)
                enableRegister(View.VISIBLE, resources.getString(R.string.register))
                setRegisterObservers()
                setRegisterListeners()
            } else {
                viewModel.setNewUser(false)
                enableRegister(View.GONE, resources.getString(R.string.login))
                removeRegisterListeners()
            }
        }
        binding.regPasswordInput.setOnFocusChangeListener{ _, b ->
            if (!b) viewModel.setPassword(getPasswordText())
            else changeConstraints()
        }
        binding.regEmailInput.setOnFocusChangeListener { _, b ->
            if (b) changeConstraints()
            else viewModel.isValidEmail(getEmailText())
        }
        binding.submit.setOnClickListener{
            if (binding.submit.text.equals(resources.getString(R.string.register))) {
                viewModel.setEmail(getEmailText())
                viewModel.setPassCon(binding.regPasswordCon.editText?.text.toString().trim { it <= ' ' })
                viewModel.submitNewUser()
            } else viewModel.loginUser(getEmailText(), getPasswordText())
        }
    }

    private fun setRegisterListeners() {
        binding.regPasswordInputConEdit.setOnFocusChangeListener { _, b ->
            if (b) changeConstraints()
        }
        binding.regEmailInput.addTextChangedListener(object : LetterWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().contains("@") && s.toString().contains("."))
                    viewModel.isValidEmail(s.toString())
            }
        })
    }

    private fun removeRegisterListeners() {
        binding.regEmailInput.addTextChangedListener(null)
    }

    private fun getEmailText(): String {
        return binding.regEmail.editText?.text.toString().trim() {it <= ' '}
    }

    private fun getPasswordText(): String {
        return binding.regPassword.editText?.text.toString().trim() {it <= ' '}
    }

    private fun changeConstraints() {
        val params = binding.regScroll.layoutParams as ConstraintLayout.LayoutParams
        params.bottomToTop = binding.submit.id
        binding.regScroll.requestLayout()
    }



}
