package com.adsama.clevertapassignment

import android.app.Application
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adsama.hundebilder.HundCallbacks
import com.adsama.hundebilder.HundMediator

class HundViewModel(application: Application) : AndroidViewModel(application), HundCallbacks {

    private val mHundMediator = HundMediator(this)

    val count = ObservableField("")

    private val _shouldShowLoadingIndicator = MutableLiveData(true)
    val shouldShowLoadingIndicator: LiveData<Boolean> get() = _shouldShowLoadingIndicator

    private val _hundResponse = MutableLiveData<String>()
    val getHundResponse: LiveData<String> get() = _hundResponse

    private var mImageUrls: ArrayList<String> = arrayListOf()
    private var currentIndex = -1

    fun requestForImage() {
        _shouldShowLoadingIndicator.value = true
        mHundMediator.getImage()
    }

    fun requestForMultipleImages() {
        _shouldShowLoadingIndicator.value = true
        mHundMediator.getMultipleImages(count.get()!!.toInt())
    }

    fun requestForNextImage() {
        if (mImageUrls.isNotEmpty()) {
            if (currentIndex < mImageUrls.size - 1) {
                currentIndex++
                _hundResponse.value = mImageUrls[currentIndex]
            } else {
                _shouldShowLoadingIndicator.value = true
                mHundMediator.getImage()
            }
        } else {
            _shouldShowLoadingIndicator.value = true
            mHundMediator.getImage()
        }
    }

    fun requestForPreviousImage() {
        if (mImageUrls.isNotEmpty()) {
            if (currentIndex > 0) {
                currentIndex--
                _hundResponse.value = mImageUrls[currentIndex]
            }
        } else {
            mHundMediator.getPreviousImage()
        }
    }

    override fun getImage(imageUrl: String) {
        _shouldShowLoadingIndicator.value = false
        _hundResponse.value = imageUrl
    }

    override fun getImages(imageUrls: ArrayList<String>) {
        _shouldShowLoadingIndicator.value = false
        mImageUrls = imageUrls
    }

    override fun getError(errorMessage: String) {
        _shouldShowLoadingIndicator.value = false
        Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun getNextImage(imageUrl: String) {
        _shouldShowLoadingIndicator.value = false
        _hundResponse.value = imageUrl
    }

    override fun getPreviousImage(imageUrl: String) {
        _shouldShowLoadingIndicator.value = false
        _hundResponse.value = imageUrl
    }

}