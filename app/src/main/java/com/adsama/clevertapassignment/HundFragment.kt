package com.adsama.clevertapassignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adsama.clevertapassignment.databinding.FragmentHundBinding
import com.adsama.clevertapassignment.utils.hideKeyboard
import com.bumptech.glide.Glide

class HundFragment : Fragment() {

    private lateinit var mHundBinding: FragmentHundBinding
    private val mHundViewModel: HundViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mHundBinding = FragmentHundBinding.inflate(inflater, container, false)
        mHundBinding.viewModel = mHundViewModel
        mHundBinding.lifecycleOwner = this
        mHundViewModel.requestForImage()
        setOnClickEvents()
        observeData()
        return mHundBinding.root
    }

    private fun setOnClickEvents() {
        mHundBinding.btnPrev.setOnClickListener {
            mHundViewModel.requestForPreviousImage()
        }
        mHundBinding.btnNext.setOnClickListener {
            mHundViewModel.requestForNextImage()
        }
        mHundBinding.btnSubmit.setOnClickListener {
            mHundViewModel.requestForMultipleImages()
            hideKeyboard(mHundBinding.root, requireContext())
        }
    }

    private fun observeData() {
        mHundViewModel.getHundResponse.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it)
                .error(AppCompatResources.getDrawable(requireContext(), R.drawable.empty_error))
                .into(mHundBinding.ivHund)
        }
        mHundViewModel.getErrorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

}