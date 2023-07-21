package com.adsama.clevertapassignment

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsama.hundebilder.HundCallbacks
import com.adsama.hundebilder.HundMediator

class HundViewModel : ViewModel(), HundCallbacks {

    private val mHundMediator = HundMediator(this)

    val count = ObservableField("")

    private val _shouldShowLoadingIndicator = MutableLiveData(true)
    val shouldShowLoadingIndicator: LiveData<Boolean> get() = _shouldShowLoadingIndicator

    private val _hundResponse = MutableLiveData<String>()
    val getHundResponse: LiveData<String> get() = _hundResponse

    private val _errorMessage = MutableLiveData<String>()
    val getErrorMessage: LiveData<String> get() = _errorMessage

    fun requestForImage() {
        _shouldShowLoadingIndicator.value = true
        mHundMediator.getImage()
    }

    fun requestForMultipleImages() {
        _shouldShowLoadingIndicator.value = true
        mHundMediator.getMultipleImages(count.get()!!.toInt())
    }

    fun requestForNextImage() {
        _shouldShowLoadingIndicator.value = true
        mHundMediator.getNextImage()
    }

    fun requestForPreviousImage() {
        mHundMediator.getPreviousImage()
    }

    override fun getImage(imageUrl: String) {
        _shouldShowLoadingIndicator.value = false
        _hundResponse.value = imageUrl
    }

    override fun getError(errorMessage: String) {
        _shouldShowLoadingIndicator.value = false
        _errorMessage.value = errorMessage
    }

    override fun getNextImage(imageUrl: String) {
        _shouldShowLoadingIndicator.value = false
        _hundResponse.value = imageUrl
    }

    override fun getPreviousImage(imageUrl: String) {
        _shouldShowLoadingIndicator.value = false
        _hundResponse.value = imageUrl
    }

    override fun onCleared() {
        super.onCleared()
        mHundMediator.cancelCoroutineScope()
    }

}