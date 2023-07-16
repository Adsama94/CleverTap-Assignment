package com.adsama.hundebilder

interface HundCallbacks {

    fun getImage(imageUrl: String)

    fun getImages(imageUrls: ArrayList<String>)

    fun getError(errorMessage: String)

    fun getNextImage(imageUrl: String)

    fun getPreviousImage(imageUrl: String)

}