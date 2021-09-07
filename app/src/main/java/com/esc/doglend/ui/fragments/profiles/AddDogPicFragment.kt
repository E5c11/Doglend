package com.esc.doglend.ui.fragments.profiles

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.esc.doglend.R
import com.esc.doglend.databinding.AddDogPicFragmentBinding
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDogPicFragment: Fragment(R.layout.add_dog_pic_fragment) {

    private lateinit var binding : AddDogPicFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddDogPicFragmentBinding.bind(view)
        getImage()
        setListeners()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data!!.data
            binding.dogCropPp.apply {
                setImageUriAsync(uri)
                guidelines = CropImageView.Guidelines.ON
                setAspectRatio(1, 1)
            }
        }
    }

    private fun getImage() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
        binding.dogCropPp.visibility = View.VISIBLE
        binding.dogPp.visibility = View.INVISIBLE
    }

    private fun cropImage() {
        binding.dogPp.setImageBitmap(binding.dogCropPp.croppedImage)
    }

    private fun setListeners() {
        binding.browse.setOnClickListener {
            getImage()
        }
        binding.crop.setOnClickListener {
            binding.dogCropPp.visibility = View.INVISIBLE
            binding.dogPp.visibility = View.VISIBLE
            cropImage()
        }
    }

}