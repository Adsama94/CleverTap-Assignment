package com.adsama.hundebilder

import com.adsama.hundebilder.model.HundResponse
import com.adsama.hundebilder.network.HundApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class HundMediator(hundCallbacks: HundCallbacks) {

    private val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mHundCallbacks = hundCallbacks
    private val mHundImages = ArrayList<String>()
    private var currentIndex = -1

    fun getImage() {
        mCoroutineScope.launch {
            try {
                val response = getSingleHundImage()
                if (response.isSuccessful) {
                    val hundSingleResponse = response.body()
                    if (hundSingleResponse!!.status == "success" && hundSingleResponse.message is String) {
                        withContext(Dispatchers.Main) {
                            val imageUrl = hundSingleResponse.message
                            mHundImages.add(imageUrl)
                            currentIndex = mHundImages.size - 1
                            mHundCallbacks.getImage(imageUrl)

                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mHundCallbacks.getError("Error retrieving data!")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mHundCallbacks.getError("Exception fetching data! ${e.message}")
                }
            }
        }
    }

    fun getMultipleImages(count: Int) {
        mCoroutineScope.launch {
            try {
                val response = getMultipleHundImages(count)
                if (response.isSuccessful) {
                    val hundMultipleResponse = response.body()
                    if (hundMultipleResponse!!.status == "success" && hundMultipleResponse.message is ArrayList<*>) {
                        withContext(Dispatchers.Main) {
                            val imageUrls = hundMultipleResponse.message.filterIsInstance<String>()
                            mHundImages.addAll(imageUrls)
                            currentIndex = mHundImages.size - 1
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mHundCallbacks.getError("Error retrieving data!")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mHundCallbacks.getError("Exception fetching data! ${e.message}")
                }
            }
        }
    }

    fun getNextImage() {
        if (currentIndex < mHundImages.size - 1) {
            currentIndex++
            val imageUrl = mHundImages[currentIndex]
            mHundCallbacks.getNextImage(imageUrl)
        } else {
            getImage()
        }
    }

    fun getPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--
            val imageUrl = mHundImages[currentIndex]
            mHundCallbacks.getImage(imageUrl)
        } else {
            mHundCallbacks.getError("No Previous Images!")
        }
    }

    private suspend fun getSingleHundImage(): Response<HundResponse> {
        return HundApi.hundService.getImageAsync().await()
    }

    private suspend fun getMultipleHundImages(count: Int): Response<HundResponse> {
        return HundApi.hundService.getImagesAsync(count).await()
    }

}